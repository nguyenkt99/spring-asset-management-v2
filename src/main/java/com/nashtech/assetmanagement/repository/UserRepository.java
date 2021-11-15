package com.nashtech.assetmanagement.repository;
import com.nashtech.assetmanagement.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity getByStaffCode(String staffCode);

    Optional<UserEntity> findByStaffCode(String staffCode);

    Optional<UserEntity> findByStaffCodeAndUserDetail_Department_Location(String staffCode, LocationEntity location);

    UserEntity getByUserName(String username);

    Optional<UserEntity> findByUserName(String username);

    List<UserEntity> findAllByUserDetail_Department_LocationOrderByStaffCodeAsc(LocationEntity location);

    @Query("SELECT u FROM UserEntity u WHERE u.role.id = 1")
    List<UserEntity> findAllAdmin();

}
