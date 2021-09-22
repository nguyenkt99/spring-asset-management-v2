package com.nashtech.AssetManagement_backend.repository;

import com.nashtech.AssetManagement_backend.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, String> {
    boolean existsByName(String name);

    @Query(value = "from CategoryEntity c where lower(c.name) = lower(:name)")
    CategoryEntity getByName(@Param("name") String name);

    @Query(value = "from CategoryEntity c where lower(c.prefix) = lower(:prefix)")
    CategoryEntity getByPrefix(@Param("prefix") String prefix);

    Optional<CategoryEntity> findByPrefix(String prefix);
}
