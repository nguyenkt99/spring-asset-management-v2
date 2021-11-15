package com.nashtech.assetmanagement.service;

import com.nashtech.assetmanagement.dto.UserDTO;
import com.nashtech.assetmanagement.payload.request.LoginRequest;

import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> authenticateUser(LoginRequest loginRequest);
    UserDTO changePasswordAfterFirstLogin(String username, String password);
    Boolean changePassword(String username, String oldPassword, String newPassword);
    Boolean validOTP(String email, int OTP);
    Boolean getOTP(String email);
}
