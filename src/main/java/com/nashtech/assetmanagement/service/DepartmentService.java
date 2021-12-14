package com.nashtech.assetmanagement.service;

import com.nashtech.assetmanagement.dto.DepartmentDTO;

import java.util.List;

public interface DepartmentService {
    List<DepartmentDTO> getDepartments(String username);
    DepartmentDTO createDepartment(DepartmentDTO dto, String username);
    DepartmentDTO updateDepartment(DepartmentDTO dto);
    void deleteDepartment(String deptCode);
}
