package com.nashtech.assetmanagement.repository;

import com.nashtech.assetmanagement.entity.RepairEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RepairRepository extends JpaRepository<RepairEntity, Long> {
    @Query("select r " +
            "from RepairEntity r " +
            "left join AssetEntity a on r.asset.assetCode = a.assetCode " +
            "where a.location.id = ?1 " +
            "order by r.id")
    List<RepairEntity> findAllByLocationId(Long locationId);
}
