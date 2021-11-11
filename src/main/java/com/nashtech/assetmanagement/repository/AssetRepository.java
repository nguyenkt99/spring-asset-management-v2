package com.nashtech.assetmanagement.repository;

import com.nashtech.assetmanagement.dto.StateQuantity;
import com.nashtech.assetmanagement.entity.AssetEntity;
import com.nashtech.assetmanagement.entity.CategoryEntity;
import com.nashtech.assetmanagement.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<AssetEntity, String> {
    Optional<AssetEntity> findByAssetName(String assetName);
    Optional<AssetEntity> findByAssetCode(String assetCode);

    @Query("select a " +
            "from AssetEntity a " +
            "where a.location.id = ?1 " +
            "order by a.assetCode asc")
    List<AssetEntity> findAll(long locationId);

    int countByCategoryEntityAndLocation(CategoryEntity category, LocationEntity location);

    @Query(nativeQuery = true, value = "select a.state, count(*) quantity \n" +
            "from asset a \n" +
            "where a.category_id = ?1 and a.location_id = ?2 \n" +
            "group by a.state \n")
    List<StateQuantity> countState(String prefix, long location);

//    @Query(value = "select a \n" +
//            "from AssetEntity a \n" +
//            "left join AssignmentDetailEntity ad on a.assetCode = ad.asset.assetCode \n" +
//            "left join AssignmentEntity a2 on ad.id.assignmentId = a2.id \n" +
//            "where a.location.id = ?1 and ((?3 < a2.assignedDate or ?2 > a2.intendedReturnDate) or ad.state = 'DECLINED')")

//    @Query(value = "select a1 \n" +
//        "from AssetEntity a1 \n" +
//        "where a1.locationId = ?1 and a1.assetCode not in (" +
//        "select a.assetCode \n" +
//        "from AssetEntity a \n" +
//        "left join AssignmentDetailEntity ad on a.assetCode = ad.asset.assetCode \n" +
//        "left join AssignmentEntity a2 on ad.id.assignmentId = a2.id \n" +
//        "where a.location.id = ?1 \n" +
//        "and (?2 between a2.assignedDate and a2.intendedReturnDate) \n" +
//        "and (?3 between a2.assignedDate and a2.intendedReturnDate) \n")
//    List<AssetEntity> findAvailableAsset(Long locationId, Date startDate, Date endDate);

    @Query(value =
        "select a \n" +
        "from AssetEntity a \n" +
        "where a.location.id = ?1 and a.assetCode not in (" +
            "select a.assetCode \n" +
            "from AssetEntity a \n" +
            "left join AssignmentDetailEntity ad on a.assetCode = ad.asset.assetCode \n" +
            "left join AssignmentEntity a2 on ad.id.assignmentId = a2.id \n" +
            "where a.location.id = ?1 " +
            "and (?2 between a2.assignedDate and a2.intendedReturnDate) \n" +
            "and (?3 between a2.assignedDate and a2.intendedReturnDate)" +
            "and a2.state != 'COMPLETED' and a2.state != 'DECLINED')")
    List<AssetEntity> findAvailableAsset(Long locationId, Date startDate, Date endDate);

}
