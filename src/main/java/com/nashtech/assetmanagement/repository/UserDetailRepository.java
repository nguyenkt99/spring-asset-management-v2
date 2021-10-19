package com.nashtech.assetmanagement.repository;

import com.nashtech.assetmanagement.entity.UserDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDetailRepository extends JpaRepository<UserDetailEntity, String> {
    Boolean existsByEmail(String email);
    Optional<UserDetailEntity> findByEmail(String email);
}
