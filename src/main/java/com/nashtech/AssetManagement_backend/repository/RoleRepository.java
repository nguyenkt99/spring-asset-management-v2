package com.nashtech.AssetManagement_backend.repository;


import com.nashtech.AssetManagement_backend.entity.RoleName;
import com.nashtech.AssetManagement_backend.entity.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RolesEntity, Long> {
    RolesEntity getByName(RoleName role);
}
