package com.nashtech.assetmanagement.repository;

import java.time.LocalDate;
import java.util.List;

import com.nashtech.assetmanagement.dto.report.IAssignmentAssigned;
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

    List<AssignmentEntity> findByAssignTo_StaffCodeAndAssignedDateIsLessThanEqualOrderByIdAsc(String staffCode, LocalDate currentDate);


    /* Report */
    @Query(value = "select \n" +
            " ad.asset_code as assetCode, \n" +
            " a2.name as assetName, \n" +
            " ( select concat(s2.first_name, ' ', s2.last_name, ' (', s2.staff_code, ')') as assignedBy\n" +
            "   from staffs s2 \n" +
            "   where s2.staff_code = a.assigned_by\n" +
            " ),\n" +
            " ( select concat(s2.first_name, ' ', s2.last_name, ' (', s2.staff_code, ')') as assignedTo\n" +
            "   from staffs s2 \n" +
            "   where s2.staff_code = a.assigned_to\n" +
            " )\n" +
            " from assignment_details ad\n" +
            " left join assets a2 on a2.asset_code = ad.asset_code \n" +
            " left join assignments a on a.id = ad.assignment_id\n" +
            " left join staffs s on s.staff_code = a.assigned_to \n" +
            " where a.state != 'DECLINED' and a.assigned_date = ?1 and a2.location_id = ?2",
            nativeQuery = true)
    List<IAssignmentAssigned> getAssignmentsAssignedByDate(LocalDate date, Long locationId);
}