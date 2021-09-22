package com.nashtech.AssetManagement_backend.repository;

import com.nashtech.AssetManagement_backend.entity.UserDetailEntity;
import com.nashtech.AssetManagement_backend.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDetailRepository extends JpaRepository<UserDetailEntity, String> {
    Boolean existsByEmail(String email);
    Optional<UserDetailEntity> findByEmail(String email);
}
