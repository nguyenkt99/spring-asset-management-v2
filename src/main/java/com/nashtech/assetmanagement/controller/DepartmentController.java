package com.nashtech.assetmanagement.controller;

import com.nashtech.assetmanagement.dto.DepartmentDTO;
import com.nashtech.assetmanagement.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/department")
public class DepartmentController {
    @Autowired
    DepartmentService departmentService;
    @GetMapping()
    public ResponseEntity<List<DepartmentDTO>> showAll(){
        return ResponseEntity.ok().body(departmentService.showAll());
    }
}
