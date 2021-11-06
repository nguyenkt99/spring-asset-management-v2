package com.nashtech.assetmanagement.repository;

import java.util.Date;
import java.util.List;

import com.nashtech.assetmanagement.entity.AssignmentEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface AssignmentRepository extends JpaRepository<AssignmentEntity, Long> {
    @Query("select a from AssignmentEntity a "
            + "join UserDetailEntity u on u = a.assignBy "
            + "where u.department.location.id = ?1 "
            + "order by a.id asc")
    List<AssignmentEntity> findAllByAdminLocation(@Param("location") long location);

    List<AssignmentEntity> findByAssignTo_StaffCodeAndAssignedDateIsLessThanEqualOrderByIdAsc(String staffCode, Date currentDate);
}