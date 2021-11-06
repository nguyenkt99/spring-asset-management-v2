package com.nashtech.assetmanagement.repository;
import com.nashtech.assetmanagement.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<UsersEntity, Long> {
    UsersEntity getByStaffCode(String staffCode);

    Optional<UsersEntity> findByStaffCode(String staffCode);

    Optional<UsersEntity> findByStaffCodeAndUserDetail_Department_Location(String staffCode, LocationEntity location);

    UsersEntity getByUserName(String username);

    Optional<UsersEntity> findByUserName(String username);


    Boolean existsByUserName(String username);


    List<UsersEntity> findAllByUserDetail_Department_LocationOrderByStaffCodeAsc(LocationEntity location);

    @Query("SELECT u FROM UsersEntity u WHERE u.role.id = 1")
    List<UsersEntity> findAllAdmin();

}
