package com.nashtech.assetmanagement.repository;
import com.nashtech.assetmanagement.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<UsersEntity, Long> {
    UsersEntity getByStaffCode(String staffCode);

    Optional<UsersEntity> findByStaffCode(String staffCode);

    Optional<UsersEntity> findByStaffCodeAndUserDetail_Location(String staffCode, LocationEntity location);

    UsersEntity getByUserName(String username);

    Optional<UsersEntity> findByUserName(String username);

//    Optional<UsersEntity> findByEmail(String email); string email

    Boolean existsByUserName(String username);

//    Boolean existsByEmail(String email);

//    List<UsersEntity> findAllByLocationAndState(LocationEntity location, UserState userState);
    List<UsersEntity> findAllByUserDetail_Location(LocationEntity location);

    @Query("SELECT u FROM UsersEntity u WHERE u.role.id = 1")
    List<UsersEntity> findAllAdmin();

}
