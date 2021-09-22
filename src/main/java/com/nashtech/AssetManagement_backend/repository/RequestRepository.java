package com.nashtech.AssetManagement_backend.repository;

import com.nashtech.AssetManagement_backend.entity.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
}
