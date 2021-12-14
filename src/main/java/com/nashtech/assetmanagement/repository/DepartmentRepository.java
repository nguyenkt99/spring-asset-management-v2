package com.nashtech.assetmanagement.repository;

import com.nashtech.assetmanagement.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, String> {
    List<DepartmentEntity> findAllByLocation_Id(Long id);

}
