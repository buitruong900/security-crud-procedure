package com.example.demo.service.impl;

import com.example.demo.entity.User;
import com.example.demo.entity.UsersRoles;
import com.example.demo.repo.UserRepository;
import com.example.demo.repo.UsersRolesRepository;
import com.example.demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UsersRolesRepository usersRolesRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Người dùng hiện tại không có : " + email));
        if(!Boolean.TRUE.equals(user.getIsEnabled())){
            throw new RuntimeException("Tài khoản chưa được kích hoạt ");
        }

        List<UsersRoles> userRoles = usersRolesRepository.findByUser(user);
        List<String> roles = userRoles.stream()
                .map(ur -> ur.getRoleId().getName())
                .collect(Collectors.toList());

        return new CustomUserDetails(user, roles);
    }
}
