package com.nashtech.AssetManagement_backend.service;

import com.nashtech.AssetManagement_backend.dto.UserDto;
import com.nashtech.AssetManagement_backend.entity.LocationEntity;
import com.nashtech.AssetManagement_backend.entity.UserDetailEntity;
import com.nashtech.AssetManagement_backend.entity.UsersEntity;
import com.nashtech.AssetManagement_backend.exception.BadRequestException;
import com.nashtech.AssetManagement_backend.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    UsersEntity findByUserName(String username);

    UserDetailEntity findByEmail(String email);

    UserDto changePasswordAfterfirstLogin(String username, String passwordEncode);

    UserDto changePassword(String username, String passwordEncode);

    UserDto saveUser(UserDto userDto, String username) throws BadRequestException;

    List<UserDto> retrieveUsers(LocationEntity location);

//    List<UserDto> retrieveUsers(Pageable pageable);

    UserDto getUserByStaffCode(String staffCode, LocationEntity location) throws ResourceNotFoundException;

    UserDto updateUser(UserDto userDto);


    LocationEntity getLocationByUserName(String userName);

    ResponseEntity<Boolean> canDisableUser(String staffCode,String admin);

    Boolean disableUser(String staffCode, String admin);

    UserDto getProfile(String username);

}
