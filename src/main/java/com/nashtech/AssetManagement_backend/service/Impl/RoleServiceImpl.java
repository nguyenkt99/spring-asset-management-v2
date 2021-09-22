package com.nashtech.AssetManagement_backend.service.Impl;

import com.nashtech.AssetManagement_backend.dto.RoleDto;
import com.nashtech.AssetManagement_backend.entity.RolesEntity;
import com.nashtech.AssetManagement_backend.repository.RoleRepository;
import com.nashtech.AssetManagement_backend.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleRepository roleRepository;
    @Override
    public List<RoleDto> listRole() {
        List<RolesEntity> list = roleRepository.findAll();
        return new RoleDto().toListDTO(list);
    }
}
