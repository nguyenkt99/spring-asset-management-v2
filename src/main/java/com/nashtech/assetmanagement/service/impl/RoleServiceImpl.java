package com.nashtech.assetmanagement.service.impl;

import com.nashtech.assetmanagement.dto.RoleDTO;
import com.nashtech.assetmanagement.entity.RoleEntity;
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
    public List<RoleDTO> listRole() {
        List<RoleEntity> list = roleRepository.findAll();
        return new RoleDTO().toListDTO(list);
    }
}
