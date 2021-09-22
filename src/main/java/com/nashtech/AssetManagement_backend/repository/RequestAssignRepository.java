package com.nashtech.AssetManagement_backend.repository;

import com.nashtech.AssetManagement_backend.entity.LocationEntity;
import com.nashtech.AssetManagement_backend.entity.RequestAssignEntity;
import com.nashtech.AssetManagement_backend.entity.UserState;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestAssignRepository extends JpaRepository<RequestAssignEntity, Long> {
    List<RequestAssignEntity> findByRequestBy_LocationAndRequestBy_StateOrderByIdAsc(LocationEntity location, UserState state);
    List<RequestAssignEntity> findByRequestBy_StaffCodeOrderByIdAsc(String staffCode);
}
