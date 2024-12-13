package com.example.demo.service;

import com.example.demo.dto.request.DeleteUrlApiToRoleDto;
import com.example.demo.dto.request.RoleAddDto;
import com.example.demo.dto.request.RoleDto;
import com.example.demo.entity.Role;

import java.util.List;

public interface RoleService {
    List<RoleDto> getAll();
    Role add(RoleAddDto roleAddDto);
    List<String> getUrlByRoleName(String name);
    void add(String roleName,List<String> nameUrl);
    void delete(DeleteUrlApiToRoleDto deleteUrlApiToRoleDto);
}
