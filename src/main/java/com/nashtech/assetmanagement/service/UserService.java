package com.nashtech.assetmanagement.service;

import com.nashtech.assetmanagement.dto.UserDto;
import com.nashtech.assetmanagement.entity.LocationEntity;
import com.nashtech.assetmanagement.entity.UserDetailEntity;
import com.nashtech.assetmanagement.entity.UsersEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    UsersEntity findByUserName(String username);

    UserDetailEntity findByEmail(String email);

    UserDto changePasswordAfterfirstLogin(String username, String passwordEncode);

    UserDto changePassword(String username, String passwordEncode);

    UserDto saveUser(UserDto userDto, String username);

    List<UserDto> retrieveUsers(LocationEntity location);

//    List<UserDto> retrieveUsers(Pageable pageable);

    UserDto getUserByStaffCode(String staffCode, LocationEntity location);

    UserDto updateUser(UserDto userDto);


    LocationEntity getLocationByUserName(String userName);

    ResponseEntity<Boolean> canDisableUser(String staffCode,String admin);

    Boolean disableUser(String staffCode, String admin);

    UserDto getProfile(String username);

}
