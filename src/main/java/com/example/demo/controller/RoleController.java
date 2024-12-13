package com.example.demo.controller;

import com.example.demo.dto.request.FunctionAddDto;
import com.example.demo.dto.request.RoleAddDto;
import com.example.demo.repo.FunctionRepository;
import com.example.demo.repo.RoleRepository;
import com.example.demo.repo.UrlApiRepository;
import com.example.demo.service.FunctionService;
import com.example.demo.service.RoleService;
import com.example.demo.service.UrlApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/manage")
public class RoleController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private FunctionService functionService;
    @Autowired
    private UrlApiService urlApiService;
    @PostMapping("/all")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(roleService.getAll());
    }
    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody RoleAddDto roleAddDto){
        return ResponseEntity.ok(roleService.add(roleAddDto));
    }


}
