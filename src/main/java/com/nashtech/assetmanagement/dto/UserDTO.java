package com.nashtech.assetmanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.assetmanagement.constants.Gender;
import com.nashtech.assetmanagement.constants.Location;
import com.nashtech.assetmanagement.constants.RoleName;
import com.nashtech.assetmanagement.constants.UserState;
import com.nashtech.assetmanagement.entity.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String staffCode;

    @Size(max = 50)
    @NotBlank(message = "First name can't not be blank")
        private String firstName;

    @Size(max = 50)
    @NotBlank(message = "Last name can't not be blank")
    private String lastName;

    @NotNull(message = "gender date is not null")
    private Gender gender;

    private String fullName;

    private String username;

    @NotNull(message = "Date of birth date is not null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dateOfBirth;

    @NotNull(message = "Joined date is not null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate joinedDate;

    private String email;

    private String deptCode;

    private String deptName;

    private Location location;

    private Boolean isFirstLogin;

    @NotNull(message = "Role name can not be null")
    private RoleName type;

    private UserState state;

    public UserDTO(UserEntity entity) {
        this.lastName = entity.getUserDetail().getLastName();
        this.firstName = entity.getUserDetail().getFirstName();
        this.dateOfBirth = entity.getUserDetail().getDateOfBirth();
        this.joinedDate = entity.getUserDetail().getJoinedDate();
        this.location = entity.getUserDetail().getDepartment().getLocation().getName();
        this.type = entity.getRole().getName();
        this.state = entity.getUserDetail().getState();
        this.gender = entity.getUserDetail().getGender();
        this.staffCode = entity.getStaffCode();
        this.username = entity.getUserName();
        this.isFirstLogin = entity.isFirstLogin();
        this.email = entity.getUserDetail().getEmail();
        this.fullName = entity.getUserDetail().getFirstName() + " " + entity.getUserDetail().getLastName();
        this.deptCode = entity.getUserDetail().getDepartment().getDeptCode();
        this.deptName = entity.getUserDetail().getDepartment().getName();
    }

    public UserEntity toEntity() {
        UserEntity entity = new UserEntity();
        UserDetailEntity userDetail = new UserDetailEntity();
        userDetail.setUser(entity);
        userDetail.setFirstName(this.firstName);
        userDetail.setLastName(this.lastName);
        userDetail.setGender(this.gender);
        userDetail.setDateOfBirth(this.dateOfBirth);
        userDetail.setJoinedDate(this.joinedDate);
        userDetail.setEmail(this.email);
        entity.setUserDetail(userDetail);
        return entity;
    }

}
