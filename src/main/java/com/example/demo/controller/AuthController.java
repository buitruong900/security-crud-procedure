package com.example.demo.controller;

import com.example.demo.dto.request.*;
import com.example.demo.repo.RoleRepository;
import com.example.demo.repo.UrlApiRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.RoleService;
import com.example.demo.service.UrlApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private UrlApiRepository urlApiRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UrlApiService urlApiService;

    @Autowired
    private RoleService roleService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }

    @PostMapping("/permissions-all")
    public ResponseEntity<?>permissionAll(){
        return ResponseEntity.ok(urlApiService.getAll());
    }

    @PostMapping("/add-permission-to-role")
    public ResponseEntity<Map<String, String>> addPermissionToRole(@RequestBody AddUrlApiToRoleDto addUrlApiToRoleDto) {
        Map<String, String> response = new HashMap<>();
        roleService.add(addUrlApiToRoleDto.getNameRole(), addUrlApiToRoleDto.getNameUrl());
        response.put("message", "Permission duoc them thanh cong: "+addUrlApiToRoleDto.getNameRole());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete-permission-to-role")
    public ResponseEntity<Map<String, String>> deletePermissionByRole(@RequestBody DeleteUrlApiToRoleDto deleteUrlApiToRoleDto) {
        Map<String, String> response = new HashMap<>();
        roleService.delete(deleteUrlApiToRoleDto);
        response.put("message", "Permission duoc xoa thanh cong: " + deleteUrlApiToRoleDto.getNameRole());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/find-apiUrl-byRole")
    public ResponseEntity<?>findUrlApiByRole(@RequestBody RoleDto roleDto){
        List<String> urls = roleService.getUrlByRoleName(roleDto.getName());
        return ResponseEntity.ok(urls);
    }

    @PostMapping("/permissions")
    public ResponseEntity<?> findUrlByRole(@RequestBody List<String> roles) {
        try {
            List<String> permissions = urlApiRepository.findNameUrlByRoles(roles);
            return ResponseEntity.ok(permissions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lấy quyền.");
        }
    }
    @PostMapping("/function-permission")
    public ResponseEntity<?>findFunctionByRole(@RequestBody List<String> roles){
        try {
            List<String> functions = roleRepository.findRoleFunctionAndPermission(roles);
            return ResponseEntity.ok(functions);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lấy function");
        }
    }
    @PostMapping("/check-otp")
    public ResponseEntity<?> checkOtp(@RequestBody UserOtpDto userOtpDto){
        return authService.checkOtp(userOtpDto);
    }
    @PostMapping("/sendBack-otp")
    public ResponseEntity<?> sendBackOtp(@RequestBody UserOtpDto userOtpDto){
        return authService.sendBackOtp(userOtpDto);
    }
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest){
        return authService.registerKhachHang(signupRequest);
    }

    @PostMapping("/signup/nhan-vien")
    public ResponseEntity<?> signupAdmin(@RequestBody SignupRequest signupRequest){
        return authService.registerNhanVien(signupRequest);
    }

    @PostMapping("/signup/staff")
    public ResponseEntity<?>signupStaff(@RequestBody SignupRequest signupRequest){
        return authService.registerStaff(signupRequest);
    }

    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestBody ExitEmailDto exitEmailDto){
        String email = exitEmailDto.getEmail();
        if(email == null && email.trim().isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("message", "Email không được để trống"));
        }
        Boolean exits= authService.userEmail(exitEmailDto.getEmail());
        return ResponseEntity.ok(Map.of("exits",exits));
    }
}
