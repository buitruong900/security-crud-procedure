package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "TBL_USER")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "USERNAME")
    private String userName;
    @Column(name = "OTP_CODE")
    private String otpCode;
    @Column(name = "OTP_GENERATED_TIME")
    private LocalDateTime otpGeneratedTime;
    @Column(name = "IS_ENABLED")
    private Boolean isEnabled = false;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsersRoles> roles = new ArrayList<>();
}
