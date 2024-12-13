package com.example.demo.jwt;

import com.example.demo.repo.RoleRepository;
import com.example.demo.repo.UrlApiRepository;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    @Value("truongbv")
    private String JWT_SECRET;
    @Value("${jwt.expiration}")
    private int JWT_EXPIRATION;
    @Value("${jwt.refresh-expiration}")
    private int JWT_REFRESH_EXPIRATION;

    //tạo access token từ thông tin xác thực
    public String genToken(Authentication authentication){
        UserDetails userDetails =(UserDetails)authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        String roles = authorities.stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.joining(","));

        return Jwts.builder()//tạo token với các thông tin :
                .setSubject(userDetails.getUsername())
                .claim("role",roles)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }
    //tao refreshToken
    public String refreshToken(String username){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_REFRESH_EXPIRATION);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512,JWT_SECRET)
                .compact();
    }

    //trích xuat thong tin hop le cua jwt
    public String getUserNameFormJwt(String token){
        Claims claims = Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET)
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
