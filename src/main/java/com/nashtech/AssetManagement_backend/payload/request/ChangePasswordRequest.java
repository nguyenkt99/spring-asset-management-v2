package com.nashtech.AssetManagement_backend.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "oldPassword name can not be empty")
    @Size(min = 8, max = 36, message = "oldPassword must be between 8 and 36")
    private String oldPassword;

    @NotBlank(message = "newPassword name can not be empty")
    @Size(min = 8, max = 36, message = "newPassword must be between 8 and 36")
    private String newPassword;
}
