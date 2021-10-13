package com.nashtech.AssetManagement_backend.repository;

import com.nashtech.AssetManagement_backend.entity.AssignmentDetailEntity;
import com.nashtech.AssetManagement_backend.entity.AssignmentEntity;
import com.nashtech.AssetManagement_backend.entity.AssignmentState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;


public interface AssignmentDetailRepository extends JpaRepository<AssignmentDetailEntity, Long> {
}