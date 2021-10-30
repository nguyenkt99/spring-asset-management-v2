package com.nashtech.assetmanagement.service;

import com.nashtech.assetmanagement.dto.CategoryDTO;
import com.nashtech.assetmanagement.dto.DepartmentDTO;

import java.util.List;

public interface DepartmentService {
    List<DepartmentDTO> showAll();
    DepartmentDTO getDepCode(String depCode);
    DepartmentDTO create(DepartmentDTO dto);
    DepartmentDTO update(DepartmentDTO dto);
    //void delete(String prefix);
}
