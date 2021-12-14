package com.nashtech.assetmanagement.controller;

import com.nashtech.assetmanagement.dto.DepartmentDTO;
import com.nashtech.assetmanagement.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/department")
public class DepartmentController {
    @Autowired
    DepartmentService departmentService;

    @GetMapping
    public List<DepartmentDTO> showAll(Authentication authentication){
        return departmentService.getDepartments(authentication.getName());
    }

    @PostMapping
    public DepartmentDTO create(@RequestBody DepartmentDTO dto, Authentication authentication) {
        return departmentService.createDepartment(dto, authentication.getName());
    }

    @PutMapping("/{deptCode}")
    public DepartmentDTO update(@RequestBody DepartmentDTO dto, @PathVariable String deptCode, Authentication authentication) {
        dto.setDeptCode(deptCode);
        return departmentService.updateDepartment(dto);
    }

    @DeleteMapping("/{deptCode}")
    public void delete(@PathVariable String deptCode) {
        departmentService.deleteDepartment(deptCode);
    }
}
