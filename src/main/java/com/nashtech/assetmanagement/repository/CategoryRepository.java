package com.nashtech.assetmanagement.repository;

import com.nashtech.assetmanagement.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, String> {
    boolean existsByName(String name);

    @Query(value = "from CategoryEntity c where lower(c.name) = lower(:name)")
    CategoryEntity getByName(@Param("name") String name);

    @Query(value = "from CategoryEntity c where lower(c.prefix) = lower(:prefix)")
    CategoryEntity getByPrefix(@Param("prefix") String prefix);

    Optional<CategoryEntity> findByPrefix(String prefix);

    @Query(nativeQuery = true, value = "select count(*)\n" +
            "from asset a \n" +
            "left join assignment_details ad on a.asset_code = ad.asset_code \n" +
            "left join assignments a2 on ad.assignment_id = a2.id \n" +
            "where a.category_id = ?1 and ((?3 < a2.assigned_date or ?2 > a2.intended_return_date) or ad.asset_code isnull or ad.state = 'DECLINED')")
    int getSumOfAvailableAssetByCategory(String prefix, Date startDate, Date endDate);
}
