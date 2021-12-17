package com.nashtech.assetmanagement.repository;

import com.nashtech.assetmanagement.dto.report.ReportNewDTO;
import com.nashtech.assetmanagement.dto.report.StateQuantity;
import com.nashtech.assetmanagement.entity.AssetEntity;
import com.nashtech.assetmanagement.entity.CategoryEntity;
import com.nashtech.assetmanagement.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
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
            "from assets a \n" +
            "where a.category_id = ?1 and a.location_id = ?2 \n" +
            "group by a.state \n")
    List<StateQuantity> countState(String prefix, long location);

    @Query(value =
        "select a \n" +
        "from AssetEntity a \n" +
        "where a.location.id = ?1 and (a.state = 'AVAILABLE' or a.state = 'ASSIGNED') and a.assetCode not in ( \n" +
            "select a.assetCode \n" +
            "from AssetEntity a \n" +
            "left join AssignmentDetailEntity ad on a.assetCode = ad.asset.assetCode \n" +
            "left join AssignmentEntity a2 on ad.id.assignmentId = a2.id \n" +
            "where not(?2 > a2.intendedReturnDate or ?3 < a2.assignedDate) \n" +
            "and ad.state != 'COMPLETED' and ad.state != 'DECLINED') \n" +
        "order by a.assetCode")
    List<AssetEntity> findAvailableAsset(Long locationId, LocalDate startDate, LocalDate endDate);


/* Report */
    @Query(value = "select c.name as category, \n" +
            " (select count(*) from assets a where a.location_id = ?1 and a.category_id = c.category_code) as total,\n" +
            " (select count(*) from assets a where a.location_id = ?1 and a.category_id = c.category_code and a.state = 'ASSIGNED') as assigned,\n" +
            " (select count(*) from assets a where a.location_id = ?1 and a.category_id = c.category_code and a.state = 'AVAILABLE') as available,\n" +
            " (select count(*) from assets a where a.location_id = ?1 and a.category_id = c.category_code and a.state = 'NOT_AVAILABLE') as notAvailable,\n" +
            " (select count(*) from assets a where a.location_id = ?1 and a.category_id = c.category_code and a.state = 'WAITING_FOR_RECYCLING') as waitingForRecycle,\n" +
            " (select count(*) from assets a where a.location_id = ?1 and a.category_id = c.category_code and a.state = 'RECYCLED') as recycled,\n" +
            " (select count(*) from assets a where a.location_id = ?1 and a.category_id = c.category_code and a.state = 'REPAIRING') as repairing\n" +
            " from categories c \n" +
            " order by c.name", nativeQuery = true)
    List<ReportNewDTO> getReports(Long locationId);
}
