package com.example.demo.service.impl;

import com.example.demo.dto.request.DeleteUrlApiToRoleDto;
import com.example.demo.dto.request.RoleAddDto;
import com.example.demo.dto.request.RoleDto;
import com.example.demo.entity.Role;
import com.example.demo.entity.UrlApi;
import com.example.demo.entity.UrlApiRole;
import com.example.demo.repo.RoleRepository;
import com.example.demo.repo.UrlApiRepository;
import com.example.demo.repo.UrlApiRoleRepository;
import com.example.demo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UrlApiRoleRepository urlApiRoleRepository;
    @Autowired
    private UrlApiRepository urlApiRepository;
    @Override
    public List<RoleDto> getAll() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(role -> new RoleDto(role.getRoleId(),role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Role add(RoleAddDto roleAddDto) {
        Role role = new Role();
        role.setName(roleAddDto.getNameRole());
        return roleRepository.save(role);
    }

    @Override
    public List<String> getUrlByRoleName(String name) {
        return urlApiRoleRepository.findUrlByRoleName(name);
    }

    @Override
    public void add(String roleName, List<String> nameUrl) {
        Role role = roleRepository.findByName(roleName).orElse(null);
        List<UrlApi> urlApis = urlApiRepository.findByNameUrl(nameUrl);
        List<UrlApiRole> urlApiRoles = urlApis.stream().map(urlApi -> {
            UrlApiRole urlApiRole = new UrlApiRole();
            urlApiRole.setRole(role);
            urlApiRole.setUrlApi(urlApi);
            return urlApiRole;
        }).collect(Collectors.toList());
        urlApiRoleRepository.saveAll(urlApiRoles);
    }

    @Override
    public void delete(DeleteUrlApiToRoleDto deleteUrlApiToRoleDto) {
        urlApiRoleRepository.deleteByRoleNameUrl(deleteUrlApiToRoleDto.getNameRole(),deleteUrlApiToRoleDto.getNameUrl());
    }


}
