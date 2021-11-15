package com.nashtech.assetmanagement.dto;

import com.nashtech.assetmanagement.constants.RoleName;
import com.nashtech.assetmanagement.entity.RoleEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RoleDTO {
    private Long id;
    private RoleName name;

    public RoleDTO toDTO(RoleEntity entity) {
        RoleDTO dto = new RoleDTO();
        dto.setName(entity.getName());
        dto.setId(entity.getId());
        return dto;
    }

    public RoleEntity toEntity(RoleDTO dto) {
        RoleEntity entity = new RoleEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }

    public List<RoleDTO> toListDTO(List<RoleEntity> listEntity) {
        List<RoleDTO> listDto = new ArrayList<>();
        listEntity.forEach(e -> {
            listDto.add(this.toDTO(e));
        });
        return listDto;
    }
}
