package com.nashtech.assetmanagement.repository;


import com.nashtech.assetmanagement.entity.RoleName;
import com.nashtech.assetmanagement.entity.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RolesEntity, Long> {
    RolesEntity getByName(RoleName role);
}
