package com.nashtech.AssetManagement_backend.controller;

import com.nashtech.AssetManagement_backend.dto.RoleDto;
import com.nashtech.AssetManagement_backend.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Autowired
    RoleService service;
    @GetMapping("")
    public ResponseEntity<List<RoleDto>> getAll() {
        List<RoleDto> dtos = service.listRole();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}
