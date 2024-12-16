package com.example.demo.jwt;

import com.example.demo.repo.RoleRepository;
import com.example.demo.repo.UrlApiRepository;
import com.example.demo.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.of;

@Slf4j
public class JwtAuthFilterAfter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UrlApiRepository urlApiRepository;
    @Autowired
    private RoleRepository roleRepository;
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // lay jwt tu request
            String jwt = getJwtFromRequest(request);
            // xac thuc jwt
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                // lay thong tin user
                String email = jwtTokenProvider.getUserNameFormJwt(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if (userDetails != null) {
                    // cap quyen xac thuc
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    // phan quyen va kiem tra quyen dua tren role
                    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
                    List<String> roleName = authorities.stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList());
                    List<String> allowedUrls = roleRepository.findRoleFunctionAndPermission(roleName);
                    allowedUrls.add("/api/auth");
                    log.info("role la : " + roleName);
                    log.info("jwt la  : " + jwt);
                    log.info("cac url duoc truy cap theo role : " + allowedUrls);

                    String requestUrl = request.getRequestURI();
                    if (!isUrlAllowed(requestUrl, allowedUrls)) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("{\"message\": \"Khong co quyen truy cap\"}");
                        return;
                    }
                }
            }
        } catch (Exception ex) {
            log.error("fail on set user Authentication", ex);
        }
        filterChain.doFilter(request, response);
    }

    private boolean isUrlAllowed(String requestUrl, List<String> allowedUrls) {
        for (String allowedUrl : allowedUrls) {
            String[] allowedUrlParts = allowedUrl.split(",");
            String allowedPath = allowedUrlParts[allowedUrlParts.length - 1];
            if (requestUrl.equalsIgnoreCase(allowedPath)) {
                return true;
            }
            if (requestUrl.toLowerCase().startsWith(allowedPath.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
