package com.nashtech.assetmanagement.service.impl;

import com.nashtech.assetmanagement.dto.DepartmentDTO;
import com.nashtech.assetmanagement.repository.DepartmentRepository;
import com.nashtech.assetmanagement.service.DepartmentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    DepartmentRepository departmentRepository;

    @Override
    public List<DepartmentDTO> showAll() {
        return departmentRepository.findAll().stream().map(DepartmentDTO::new).collect(Collectors.toList());
    }

    @Override
    public DepartmentDTO create(DepartmentDTO dto) {
        return null;
    }

    @Override
    public DepartmentDTO update(DepartmentDTO dto) {
        return null;
    }
}
