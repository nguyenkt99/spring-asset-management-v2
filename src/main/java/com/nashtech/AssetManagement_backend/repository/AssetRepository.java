package com.nashtech.AssetManagement_backend.repository;

import com.nashtech.AssetManagement_backend.dto.StateQuantity;
import com.nashtech.AssetManagement_backend.entity.AssetEntity;
import com.nashtech.AssetManagement_backend.entity.CategoryEntity;
import com.nashtech.AssetManagement_backend.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<AssetEntity, String> {
//    AssetEntity getByAssetName(String assetName);
    Optional<AssetEntity> findByAssetName(String assetName);
    Optional<AssetEntity> findByAssetCode(String assetCode);


    int countByCategoryEntityAndLocation(CategoryEntity category, LocationEntity location);

    @Query(nativeQuery = true, value = "select a.state, count(*) quantity \n" +
            "from asset a \n" +
            "where a.category_id = ?1 and a.location_id = ?2 \n" +
            "group by a.state \n")
    List<StateQuantity> countState(String prefix, long location);

}
