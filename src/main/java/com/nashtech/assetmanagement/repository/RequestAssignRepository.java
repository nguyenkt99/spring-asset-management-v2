package com.nashtech.assetmanagement.repository;

import com.nashtech.assetmanagement.entity.LocationEntity;
import com.nashtech.assetmanagement.entity.RequestAssignEntity;
import com.nashtech.assetmanagement.constants.UserState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestAssignRepository extends JpaRepository<RequestAssignEntity, Long> {
    List<RequestAssignEntity> findByRequestAssignBy_LocationAndRequestAssignBy_StateOrderByIdAsc(LocationEntity location, UserState state);
    List<RequestAssignEntity> findByRequestAssignBy_StaffCodeOrderByIdAsc(String staffCode);
}
