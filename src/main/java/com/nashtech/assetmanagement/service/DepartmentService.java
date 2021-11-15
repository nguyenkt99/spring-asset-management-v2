package com.nashtech.assetmanagement.service;

import com.nashtech.assetmanagement.dto.DepartmentDTO;

import java.util.List;

public interface DepartmentService {
    List<DepartmentDTO> showAll();
    DepartmentDTO create(DepartmentDTO dto);
    DepartmentDTO update(DepartmentDTO dto);
}
