package com.nashtech.assetmanagement.repository;

import com.nashtech.assetmanagement.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, String> {
}
