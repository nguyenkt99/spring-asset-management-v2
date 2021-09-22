package com.nashtech.AssetManagement_backend.payload.response;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String staffCode;
    private String username;
    private String role;
    private boolean isFirstLogin;

    public JwtResponse(String accessToken, String staffCode, String username, String role, boolean isFirstLogin) {
        this.token = accessToken;
        this.staffCode = staffCode;
        this.username = username;
        this.role = role;
        this.isFirstLogin = isFirstLogin;
    }
}