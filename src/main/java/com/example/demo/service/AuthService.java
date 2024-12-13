package com.example.demo.service;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.SignupRequest;
import com.example.demo.dto.request.UserOtpDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> login(LoginRequest loginRequest);
    ResponseEntity<?> registerNhanVien(SignupRequest signupRequest);
    ResponseEntity<?> registerKhachHang(SignupRequest signupRequest);
    Boolean userEmail(String email);
    ResponseEntity<?> registerStaff(SignupRequest signupRequest);
    ResponseEntity<?> checkOtp(UserOtpDto userOtpDto);
}
