package com.nashtech.assetmanagement.repository;

import com.nashtech.assetmanagement.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
}
