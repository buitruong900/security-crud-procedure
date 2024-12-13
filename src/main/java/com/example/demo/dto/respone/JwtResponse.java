package com.example.demo.dto.respone;

import java.util.List;
import java.util.Map;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String userName;
    private String email;
    private String password;
    private List<String> roles;
    private String refreshToken;

    public JwtResponse(String token, String userName, String email, String password, List<String> roles,String refreshToken) {
        this.token = token;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
