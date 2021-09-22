package com.nashtech.AssetManagement_backend.dto;

import com.nashtech.AssetManagement_backend.entity.RoleName;
import com.nashtech.AssetManagement_backend.entity.RolesEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RoleDto {
    private Long id;
    private RoleName name;

    public RoleDto toDTO(RolesEntity entity) {
        RoleDto dto = new RoleDto();
        dto.setName(entity.getName());
        dto.setId(entity.getId());
        return dto;
    }

    public RolesEntity toEntity(RoleDto dto) {
        RolesEntity entity = new RolesEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        return entity;
    }

    public List<RoleDto> toListDTO(List<RolesEntity> listEntity) {
        List<RoleDto> listDto = new ArrayList<>();
        listEntity.forEach(e -> {
            listDto.add(this.toDTO(e));
        });
        return listDto;
    }
}
