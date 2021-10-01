package com.nashtech.AssetManagement_backend.repository;

import com.nashtech.AssetManagement_backend.entity.RequestReturnEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<RequestReturnEntity, Long> {
}
