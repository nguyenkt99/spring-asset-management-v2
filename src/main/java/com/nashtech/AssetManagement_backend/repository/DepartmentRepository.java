package com.nashtech.AssetManagement_backend.repository;

import com.nashtech.AssetManagement_backend.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, String> {
}
