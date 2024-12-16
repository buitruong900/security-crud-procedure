package com.example.demo.service.impl;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.SignupRequest;
import com.example.demo.dto.request.UserOtpDto;
import com.example.demo.dto.respone.JwtResponse;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.UsersRoles;
import com.example.demo.jwt.JwtTokenProvider;
import com.example.demo.repo.RoleRepository;
import com.example.demo.repo.UrlApiRepository;
import com.example.demo.repo.UserRepository;
import com.example.demo.repo.UsersRolesRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.security.OtpUtil;
import com.example.demo.service.AuthService;
import com.example.demo.service.SendMailService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UsersRolesRepository usersRolesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SendMailService sendMailService;
    @Override
    public ResponseEntity<?> login(LoginRequest loginRequest) {
        if(loginRequest.getEmail()== null || loginRequest.getEmail().isEmpty()){
            return ResponseEntity.badRequest().body("Email không được để trống");
        }
        if(loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()){
            return ResponseEntity.badRequest().body("Password không được để trống");
        }
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()-> new UsernameNotFoundException("Email chưa được đăng kí"));
        if(!user.getIsEnabled()){
            return ResponseEntity.badRequest().body("Tài khoản chưa được kích hoạt");
        }
        //xác thực người dùng với email và mật khẩu
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.genToken(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            String refreshToken = jwtTokenProvider.refreshToken(user.getUserName());
            return ResponseEntity.ok(new JwtResponse(token,userDetails.getUsername(),userDetails.getEmail(),userDetails.getPassword(),roles,refreshToken));
        }catch (BadCredentialsException ex){
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }catch (ExpiredJwtException ex){
            throw  new RuntimeException("Acces token hết hạn vui lòng làm mới !");
        }
    }

    @Override
    public ResponseEntity<?> registerNhanVien(SignupRequest signupRequest) {
        return registerUser(signupRequest,"ROLE_ADMIN");
    }

    @Override
    public ResponseEntity<?> registerKhachHang(SignupRequest signupRequest) {
        return registerUser(signupRequest,"ROLE_USER");
    }

    @Override
    public ResponseEntity<?> registerStaff(SignupRequest signupRequest) {
        return registerUser(signupRequest,"ROLE_STAFF");
    }

    @Override
    public Boolean userEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private ResponseEntity<?> registerUser(SignupRequest signupRequest,String roleName){
        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity.badRequest().body("Lỗi email đang được sử dụng");
        }
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setUserName(signupRequest.getUserName());
        user.setIsEnabled(false);
        String otpCode = OtpUtil.generateOtp(6);
        user.setOtpCode(otpCode);
        user.setOtpGeneratedTime(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    return roleRepository.save(newRole);
                });

        UsersRoles usersRoles = new UsersRoles();
        usersRoles.setUser(savedUser);
        usersRoles.setRoleId(role);
        usersRolesRepository.save(usersRoles);
        // gui email otp
        String emailBody =buildEmail(savedUser.getUserName(),otpCode);
        sendMailService.sendMail(signupRequest.getEmail(),emailBody,"Xác nhận đăng ký tài khoản - Mã OTP");
        return ResponseEntity.ok(Map.of("message","Người dùng đã được đăng ký. Vui lòng kiểm tra email để nhận mã OTP"));
    }

    @Override
    public ResponseEntity<?> checkOtp(UserOtpDto userOtpDto) {
        User user = userRepository.findByEmail(userOtpDto.getEmail()).orElseThrow(() -> new RuntimeException("Không tìm thấy email người dùng"));
        if(user.getOtpGeneratedTime() == null || user.getOtpCode() == null){
            return ResponseEntity.badRequest().body("Mã otp không tồn tại hoặc đã hết thời gian");
        }
        if(user.getOtpCode() == null || !user.getOtpCode().trim().equals(userOtpDto.getOtpCode())){
            return ResponseEntity.badRequest().body("Mã otp không hợp lệ");
        }
        if(Duration.between(user.getOtpGeneratedTime(),LocalDateTime.now()).getSeconds() > 60){
            return ResponseEntity.badRequest().body("Mã otp đã hết hạn");
        }
        user.setIsEnabled(true);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message","Kích hoạt tài khoản thành công"));

    }

    @Override
    public ResponseEntity<?> sendBackOtp(UserOtpDto userOtpDto) {
        User user = userRepository.findByEmail(userOtpDto.getEmail()).orElseThrow(()-> new RuntimeException("Không tìm thấy email người dùng này "));
        if(user.getIsEnabled()){
            return ResponseEntity.ok(Map.of("message","Tài khoản của người dùng chưa được kích hoạt"));
        }
        String newOtp = OtpUtil.generateOtp(6);
        user.setOtpCode(newOtp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
        String emailBody = buildEmail(user.getUserName(), newOtp);
        sendMailService.sendMail(userOtpDto.getEmail(), emailBody, "OTP mới của bạn");
        return ResponseEntity.ok(Map.of("message", "Mã OTP mới đã được gửi đến email của bạn."));
    }

    private String buildEmail(String name, String otpCode) {
        return String.format("""
        <div style="font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c">
            <table role="presentation" width="100%%" style="border-collapse:collapse;min-width:100%%;width:100%%!important" cellpadding="0" cellspacing="0" border="0">
                <tr>
                    <td bgcolor="#0b0c0c" style="padding:20px;text-align:center;">
                        <span style="font-size:28px;line-height:1.3;color:#ffffff;font-weight:bold;">Xác nhận đăng ký tài khoản</span>
                    </td>
                </tr>
            </table>
            <table align="center" cellpadding="0" cellspacing="0" border="0" style="max-width:580px;width:100%%;margin:20px auto;">
                <tr>
                    <td style="font-size:19px;line-height:1.6;color:#0b0c0c;padding:10px 20px;">
                        <p>Chào <strong>%s</strong>,</p>
                        <p>Cảm ơn bạn đã đăng ký tài khoản. Đây là mã OTP của bạn:</p>
                        <p style="font-size:24px;font-weight:bold;text-align:center;color:#1D70B8;">%s</p>
                        <p>Mã OTP này sẽ hết hạn sau 1 phút. Vui lòng không chia sẻ mã này với bất kỳ ai.</p>
                        <p>Hẹn gặp lại!</p>
                    </td>
                </tr>
            </table>
        </div>
        """, name, otpCode);
    }

}
