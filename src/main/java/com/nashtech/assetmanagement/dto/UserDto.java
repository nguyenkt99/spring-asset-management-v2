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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
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
    private Date dateOfBirth;

    @NotNull(message = "Joined date is not null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date joinedDate;

    private String email;

    private String deptCode;

    private String deptName;

    private Location location;

    private boolean isFirstLogin;

    @NotNull(message = "Role name can not be null")
    private RoleName type;

    private UserState state;

    public UserDto(UsersEntity entity) {
        this.lastName = entity.getUserDetail().getLastName();
        this.firstName = entity.getUserDetail().getFirstName();
        this.dateOfBirth = entity.getUserDetail().getDateOfBirth();
        this.joinedDate = entity.getUserDetail().getJoinedDate();
        this.location = entity.getUserDetail().getLocation().getName();
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

    public UsersEntity toEntity(UserDto dto) {
        UsersEntity entity = new UsersEntity();
        UserDetailEntity userDetail = new UserDetailEntity();
        userDetail.setUser(entity);
        userDetail.setFirstName(dto.getFirstName());
        userDetail.setLastName(dto.getLastName());
        userDetail.setDateOfBirth(dto.getDateOfBirth());
        userDetail.setJoinedDate(dto.getJoinedDate());
        userDetail.setGender(dto.getGender());
        userDetail.setEmail(dto.getEmail());
        userDetail.setState(dto.getState());
        entity.setFirstLogin(dto.isFirstLogin);
        entity.setUserDetail(userDetail);
        return entity;
    }

    public List<UserDto> toListDto(List<UsersEntity> listEntity) {
        return listEntity.stream().map(UserDto::new).collect(Collectors.toList());
    }

}
