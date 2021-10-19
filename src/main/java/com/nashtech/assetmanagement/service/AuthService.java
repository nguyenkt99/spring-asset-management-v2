package com.nashtech.assetmanagement.service;

import com.nashtech.assetmanagement.dto.UserDto;
import com.nashtech.assetmanagement.payload.request.LoginRequest;

import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> authenticateUser(LoginRequest loginRequest);
    UserDto changePasswordAfterfirstLogin(String username, String password);
    Boolean changepassword(String username, String oldPassword, String newPassword);
    Boolean validOTP(String email, int OTP);
    Boolean getOTP(String email);
}
