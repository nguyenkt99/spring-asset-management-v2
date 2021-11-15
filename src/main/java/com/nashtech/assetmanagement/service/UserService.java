package com.nashtech.assetmanagement.service;

import com.nashtech.assetmanagement.dto.UserDTO;
import com.nashtech.assetmanagement.entity.UserDetailEntity;
import com.nashtech.assetmanagement.entity.UserEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    UserEntity findByUserName(String username);

    UserDetailEntity findByEmail(String email);

    UserDTO changePasswordAfterFirstLogin(String username, String passwordEncode);

    UserDTO changePassword(String username, String passwordEncode);

    UserDTO saveUser(UserDTO userDto, String username);

    List<UserDTO> retrieveUsers(String username);

    UserDTO getUserByStaffCode(String staffCode, String username);

    UserDTO updateUser(UserDTO userDto);

    ResponseEntity<Boolean> canDisableUser(String staffCode,String admin);

    Boolean disableUser(String staffCode, String admin);

    UserDTO getProfile(String username);

    List<UserDTO> getAdmins();

}
