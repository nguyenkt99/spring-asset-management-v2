package com.nashtech.AssetManagement_backend.repository;
import com.nashtech.AssetManagement_backend.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

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

}
