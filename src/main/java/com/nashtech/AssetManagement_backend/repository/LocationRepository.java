package com.nashtech.AssetManagement_backend.repository;

import com.nashtech.AssetManagement_backend.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
}
