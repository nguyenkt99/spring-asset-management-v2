package com.nashtech.assetmanagement.service.impl;

import com.nashtech.assetmanagement.dto.DepartmentDTO;
import com.nashtech.assetmanagement.entity.CategoryEntity;
import com.nashtech.assetmanagement.entity.DepartmentEntity;
import com.nashtech.assetmanagement.entity.LocationEntity;
import com.nashtech.assetmanagement.exception.ConflictException;
import com.nashtech.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.assetmanagement.repository.DepartmentRepository;
import com.nashtech.assetmanagement.repository.UserRepository;
import com.nashtech.assetmanagement.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public List<DepartmentDTO> getDepartments(String username) {
        LocationEntity location = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"))
                .getUserDetail().getDepartment().getLocation();
        return departmentRepository.findAllByLocation_Id(location.getId())
                .stream().map(DepartmentDTO::new).collect(Collectors.toList());
    }

    @Override
    public DepartmentDTO createDepartment(DepartmentDTO dto, String username) {
        LocationEntity location = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"))
                .getUserDetail().getDepartment().getLocation();
        DepartmentEntity department = dto.toEntity();
        department.setLocation(location);
        return new DepartmentDTO(departmentRepository.save(department));
    }

    @Override
    public DepartmentDTO updateDepartment(DepartmentDTO dto) {
        DepartmentEntity department = departmentRepository.findById(dto.getDeptCode())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found!"));
        department.setName(dto.getName());
        return new DepartmentDTO(departmentRepository.save(department));
    }

    @Override
    public void deleteDepartment(String deptCode) {
        DepartmentEntity department = departmentRepository.findById(deptCode)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));
        if (department.getUserDetailEntities().size() > 0) {
            throw new ConflictException("This department already has user!");
        }
        departmentRepository.deleteById(deptCode);
    }
}
