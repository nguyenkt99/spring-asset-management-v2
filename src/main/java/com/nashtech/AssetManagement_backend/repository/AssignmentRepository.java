package com.nashtech.AssetManagement_backend.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import com.nashtech.AssetManagement_backend.entity.AssignmentEntity;
import com.nashtech.AssetManagement_backend.entity.AssignmentState;
import com.nashtech.AssetManagement_backend.entity.Location;

import com.nashtech.AssetManagement_backend.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface AssignmentRepository extends JpaRepository<AssignmentEntity, Long> {
    @Query("select a from AssignmentEntity a "
            + "join UserDetailEntity u on u = a.assignBy "
            + "where u.location.id = ?1 "
            + "order by a.id asc")
    List<AssignmentEntity> findAllByAdminLocation(@Param("location") long location);

    List<AssignmentEntity> findByAssignTo_StaffCodeAndAssignedDateIsLessThanEqualOrderByIdAsc(String staffCode, Date currentDate);
}