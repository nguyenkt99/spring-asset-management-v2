package com.nashtech.assetmanagement.service.impl;

import com.nashtech.assetmanagement.constants.AssignmentState;
import com.nashtech.assetmanagement.constants.UserState;
import com.nashtech.assetmanagement.dto.UserDto;
import com.nashtech.assetmanagement.entity.*;
import com.nashtech.assetmanagement.exception.BadRequestException;
import com.nashtech.assetmanagement.exception.ConflictException;
import com.nashtech.assetmanagement.exception.InvalidInputException;
import com.nashtech.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.assetmanagement.repository.*;
import com.nashtech.assetmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDetailRepository userDetailRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Override
    public UsersEntity findByUserName(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("Could not found user: " + username));
    }

    @Override
    public UserDetailEntity findByEmail(String email) {
        return userDetailRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("Could not found user: " + email));
    }

    @Override
    public UserDto changePasswordAfterfirstLogin(String username, String passwordEncode) {
        UsersEntity existUser = findByUserName(username);
        existUser.setPassword(passwordEncode);
        existUser.setFirstLogin(false);

        try {
            UsersEntity user = userRepository.save(existUser);
            return new UserDto(user);
        } catch (Exception e) {
            throw new BadRequestException("invalid Request");
        }
    }

    @Override
    public UserDto changePassword(String username, String passwordEncode) {
        UsersEntity existUser = findByUserName(username);
        existUser.setPassword(passwordEncode);

        try {
            UsersEntity user = userRepository.save(existUser);
            return new UserDto(user);
        } catch (Exception e) {
            throw new BadRequestException("invalid Request");
        }
    }

    @Override
    public UserDto saveUser(UserDto userDto, String username) {
//        LocationEntity location = userRepository.findByUserName(username)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found!")).getUserDetail().getLocation();
        DepartmentEntity department = departmentRepository.findById(userDto.getDeptCode())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found!"));
        UsersEntity usersEntity = userDto.toEntity(userDto);
//        usersEntity.getUserDetail().setLocation(location);
        usersEntity.getUserDetail().setDepartment(department);
        // validate
        if (usersEntity.getUserDetail().getJoinedDate().before(usersEntity.getUserDetail().getDateOfBirth()))
            throw new InvalidInputException(
                    "Joined date is not later than Date of Birth. Please select a different date");
        if (!checkAge(usersEntity.getUserDetail().getDateOfBirth(), usersEntity.getUserDetail().getJoinedDate()))
            throw new InvalidInputException("User is under 18. Please select a different date");
        int day = getDayNumberOld(usersEntity.getUserDetail().getJoinedDate());
        if (day == 7 || day == 1)
            throw new InvalidInputException("Joined date is Saturday or Sunday. Please select a different date");
        if(userDetailRepository.existsByEmail(userDto.getEmail()))
            throw new InvalidInputException("Email is exists");
        usersEntity.setFirstLogin(true);
        RolesEntity rolesEntity = roleRepository.getByName(userDto.getType());
        usersEntity.setRole(rolesEntity);
        usersEntity = userRepository.save(usersEntity);
        return new UserDto(userRepository.getByStaffCode(usersEntity.getStaffCode()));
    }

    @Override
    public List<UserDto> retrieveUsers(String username) {
        LocationEntity location = userRepository.getByUserName(username).getUserDetail().getDepartment().getLocation();
        List<UsersEntity> usersEntities = userRepository.findAllByUserDetail_Department_LocationOrderByStaffCodeAsc(location);
        return usersEntities.stream().map(UserDto::new).collect(Collectors.toList());
    }


    @Override
    public UserDto getUserByStaffCode(String staffCode, LocationEntity location) {
        UsersEntity user = userRepository.findByStaffCodeAndUserDetail_Department_Location(staffCode, location)
                .orElseThrow(() -> new ResourceNotFoundException("user not found for this staff code: " + staffCode));
        return new UserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        UsersEntity existUser = userRepository.findByStaffCode(userDto.getStaffCode()).orElseThrow(
                () -> new ResourceNotFoundException("User not found for this staff code: " + userDto.getStaffCode()));
        DepartmentEntity department = departmentRepository.findById(userDto.getDeptCode())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found!"));
        RolesEntity rolesEntity = roleRepository.getByName(userDto.getType());

        if (userDto.getJoinedDate().before(userDto.getDateOfBirth()))
            throw new InvalidInputException(
                    "Joined date is not later than Date of Birth. Please select a different date");
        if (!checkAge(userDto.getDateOfBirth(), userDto.getJoinedDate()))
            throw new InvalidInputException("User is under 18. Please select a different date");
        int day = getDayNumberOld(userDto.getJoinedDate());
        if (day == 7 || day == 1)
            throw new InvalidInputException("Joined date is Saturday or Sunday. Please select a different date");
        if(existUser.getUserDetail().getEmail()!=null)
        {
            if(!existUser.getUserDetail().getEmail().equals(userDto.getEmail()))
                if(userDetailRepository.existsByEmail(userDto.getEmail()))
                    throw new InvalidInputException("Email is exists");
        }else
        {
            if(userDetailRepository.existsByEmail(userDto.getEmail()))
                throw new InvalidInputException("Email is exists");
        }
        existUser.getUserDetail().setDateOfBirth(userDto.getDateOfBirth());
        existUser.getUserDetail().setGender(userDto.getGender());
        existUser.getUserDetail().setJoinedDate(userDto.getJoinedDate());
        existUser.getUserDetail().setEmail(userDto.getEmail());
        existUser.getUserDetail().setDepartment(department);
        existUser.setRole(rolesEntity);

        return new UserDto(userRepository.save(existUser));
    }

    public LocationEntity getLocationByUserName(String userName) {
        return userRepository.getByUserName(userName).getUserDetail().getDepartment().getLocation();
    }

    private final String USER_NOT_FOUND = "user is not found.";
    private final String DISABLE_CONFLICT = "There are valid assignments belonging to this user. Please close all assignments before disabling user.";

    @Override
    public ResponseEntity<Boolean> canDisableUser(String staffCode, String admin) {
        UserDetailEntity usersEntity = userRepository.findByStaffCode(staffCode)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND)).getUserDetail();
        if(usersEntity.getUser().getUserName().equals(admin)) {
            throw new BadRequestException("You cannot disable yourself!");
        }

        for(AssignmentEntity assignment : usersEntity.getAssignmentTos()) {
            if(assignment.getState().equals(AssignmentState.WAITING_FOR_ACCEPTANCE) ||
                    assignment.getState().equals(AssignmentState.ACCEPTED)) {
                throw new ConflictException(DISABLE_CONFLICT);
            }
        }

        if(usersEntity.getAssignmentTos().size() > 0 || usersEntity.getAssignmentsBys().size() > 0) {
            return ResponseEntity.ok(true); //200 for disable
        } else {
            return ResponseEntity.accepted().body(true);// 202 for delete
        }
    }

    @Override
    public Boolean disableUser(String staffCode, String admin) {
        UsersEntity usersEntity = userRepository.findByStaffCode(staffCode)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        if(usersEntity.getUserName().equals(admin)) {
            throw new BadRequestException("You cannot disable yourself!");
        }

        // admin cannot disable user when user has assignment in WAITING_FOR_ACCEPTANCE or ACCEPTED state
        for(AssignmentEntity assignment : usersEntity.getUserDetail().getAssignmentTos()) {
            if(assignment.getState().equals(AssignmentState.WAITING_FOR_ACCEPTANCE) ||
                    assignment.getState().equals(AssignmentState.ACCEPTED)) {
                throw new ConflictException(DISABLE_CONFLICT);
            }
        }

        if (usersEntity.getUserDetail().getAssignmentTos().size() > 0||
                usersEntity.getUserDetail().getAssignmentsBys().size() > 0||
                usersEntity.getUserDetail().getRequestAssignBy().size() > 0||
                usersEntity.getUserDetail().getRequestBys().size() > 0||
                usersEntity.getUserDetail().getAcceptBys().size() > 0) {
            usersEntity.getUserDetail().setState(UserState.Disabled);
            userRepository.save(usersEntity);
        } else {
            userRepository.delete(usersEntity);
        }
        return true;
    }

    @Override
    public UserDto getProfile(String username) {
        UsersEntity user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        return new UserDto(user);
    }

    @Override
    public List<UserDto> getAdmins() {
        List<UsersEntity> admins = userRepository.findAllAdmin();
        return admins.stream().map(UserDto::new).collect(Collectors.toList());
    }

    // number ranges from 1 (Sunday) to 7 (Saturday)
    private int getDayNumberOld(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    // check age >=18
    private boolean checkAge(Date dOB, Date joinDate) {
        LocalDate date1 = dOB.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate date2 = joinDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period period = Period.between(date1, date2);
        return period.getYears() >= 18 ? true : false;
    }

}
