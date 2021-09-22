package com.nashtech.AssetManagement_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.AssetManagement_backend.entity.*;
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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String staffCode;

    @Size(max = 50)
    @NotBlank(message = "firstname can't not be blank")
    private String firstName;

    @Size(max = 50)
    @NotBlank(message = "lastname can't not be blank")
    private String lastName;

    @NotNull(message = "gender date is not null")
    private Gender gender;

    private String fullName;

    private String username;

    @NotNull(message = "dateOfBirth date is not null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date dateOfBirth;

    @NotNull(message = "joinded date is not null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date joinedDate;

    private String email;
    // @NotNull(message = "location is not chosen")
    private Location location;

    private boolean isFirstLogin;

    @NotNull(message = "Role name can not be null")
    private RoleName type;

    private UserState state;

    public UserDto toDto(UsersEntity entity) {
        UserDto dto = new UserDto();
        dto.setLastName(entity.getUserDetail().getLastName());
        dto.setFirstName(entity.getUserDetail().getFirstName());
        dto.setDateOfBirth(entity.getUserDetail().getDateOfBirth());
        dto.setJoinedDate(entity.getUserDetail().getJoinedDate());
        dto.setLocation(entity.getUserDetail().getLocation().getName());
        dto.setType(entity.getRole().getName());
        dto.setState(entity.getUserDetail().getState());
        dto.setGender(entity.getUserDetail().getGender());
        dto.setStaffCode(entity.getStaffCode());
        dto.setUsername(entity.getUserName());
        dto.setFirstLogin(entity.isFirstLogin());
        dto.setEmail(entity.getUserDetail().getEmail());
        dto.setFullName(entity.getUserDetail().getFirstName() + " " + entity.getUserDetail().getLastName());
        return dto;
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
        List<UserDto> listDto = new ArrayList<>();

        listEntity.forEach(e -> {
            listDto.add(this.toDto(e));
        });

        return listDto;
    }

}
