package com.nashtech.assetmanagement.service.impl;

import com.nashtech.assetmanagement.dto.RoleDto;
import com.nashtech.assetmanagement.entity.RolesEntity;
import com.nashtech.assetmanagement.repository.RoleRepository;
import com.nashtech.assetmanagement.service.RoleService;
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
