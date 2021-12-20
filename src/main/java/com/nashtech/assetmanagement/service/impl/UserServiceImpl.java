package com.nashtech.assetmanagement.service.impl;

import com.nashtech.assetmanagement.constants.AssignmentState;
import com.nashtech.assetmanagement.constants.UserState;
import com.nashtech.assetmanagement.dto.UserDTO;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDetailRepository userDetailRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Override
    public UserEntity findByUserName(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("Could not found user: " + username));
    }

    @Override
    public UserDetailEntity findByEmail(String email) {
        return userDetailRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("Could not found user: " + email));
    }

    @Override
    public UserDTO changePasswordAfterFirstLogin(String username, String passwordEncode) {
        UserEntity existUser = findByUserName(username);
        existUser.setPassword(passwordEncode);
        existUser.setFirstLogin(false);

        try {
            UserEntity user = userRepository.save(existUser);
            return new UserDTO(user);
        } catch (Exception e) {
            throw new BadRequestException("invalid Request");
        }
    }

    @Override
    public UserDTO changePassword(String username, String passwordEncode) {
        UserEntity existUser = findByUserName(username);
        existUser.setPassword(passwordEncode);

        try {
            UserEntity user = userRepository.save(existUser);
            return new UserDTO(user);
        } catch (Exception e) {
            throw new BadRequestException("invalid Request");
        }
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO, String username) {
        // validate
        if (userDTO.getJoinedDate().isBefore(userDTO.getDateOfBirth()))
            throw new InvalidInputException("Joined date is not later than Date of Birth. Please select a different date");
        if (!checkAge(userDTO.getDateOfBirth(), userDTO.getJoinedDate()))
            throw new InvalidInputException("User is under 18. Please select a different date");
        DayOfWeek dayOfWeek = userDTO.getJoinedDate().getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SATURDAY)
            throw new InvalidInputException("Joined date is Saturday or Sunday. Please select a different date");

        DepartmentEntity department = departmentRepository.findById(userDTO.getDeptCode())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found!"));
        RoleEntity role = roleRepository.findByName(userDTO.getType())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found!"));

        if(userDetailRepository.existsByEmail(userDTO.getEmail()))
            throw new InvalidInputException("Email is exists");

        UserEntity userEntity = userDTO.toEntity();
        userEntity.getUserDetail().setDepartment(department);
        userEntity.getUserDetail().setState(UserState.ENABLED);
        userEntity.setRole(role);
        userEntity.setFirstLogin(true);
        return new UserDTO(userRepository.save(userEntity));
    }

    @Override
    public List<UserDTO> retrieveUsers(String username) {
        LocationEntity location = userRepository.getByUserName(username).getUserDetail().getDepartment().getLocation();
        List<UserEntity> usersEntities = userRepository.findAllByUserDetail_Department_LocationOrderByStaffCodeAsc(location);
        return usersEntities.stream().map(UserDTO::new).collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserByStaffCode(String staffCode, String username) {
        LocationEntity location = userRepository.getByUserName(username).getUserDetail().getDepartment().getLocation();
        UserEntity user = userRepository.findByStaffCodeAndUserDetail_Department_Location(staffCode, location)
                .orElseThrow(() -> new ResourceNotFoundException("user not found for this staff code: " + staffCode));
        return new UserDTO(user);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        // validate
        if (userDTO.getJoinedDate().isBefore(userDTO.getDateOfBirth()))
            throw new InvalidInputException("Joined date is not later than Date of Birth. Please select a different date");
        if (!checkAge(userDTO.getDateOfBirth(), userDTO.getJoinedDate()))
            throw new InvalidInputException("User is under 18. Please select a different date");
        DayOfWeek dayOfWeek = userDTO.getJoinedDate().getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SATURDAY)
            throw new InvalidInputException("Joined date is Saturday or Sunday. Please select a different date");

        UserEntity existUser = userRepository.findByStaffCode(userDTO.getStaffCode()).orElseThrow(
                () -> new ResourceNotFoundException("User not found for this staff code: " + userDTO.getStaffCode()));
        DepartmentEntity department = departmentRepository.findById(userDTO.getDeptCode())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found!"));
        RoleEntity role = roleRepository.findByName(userDTO.getType())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found!"));

        if(!existUser.getUserDetail().getEmail().equalsIgnoreCase(userDTO.getEmail())
                && userDetailRepository.existsByEmail(userDTO.getEmail()))
                throw new InvalidInputException("Email is exists!");

        existUser.getUserDetail().setDateOfBirth(userDTO.getDateOfBirth());
        existUser.getUserDetail().setGender(userDTO.getGender());
        existUser.getUserDetail().setJoinedDate(userDTO.getJoinedDate());
        existUser.getUserDetail().setEmail(userDTO.getEmail());
        existUser.getUserDetail().setDepartment(department);
        existUser.setRole(role);

        return new UserDTO(userRepository.save(existUser));
    }

    @Override
    public ResponseEntity<Boolean> canDisableUser(String staffCode, String admin) {
        UserDetailEntity userDetail = userRepository.findByStaffCode(staffCode)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND)).getUserDetail();
        if(userDetail.getUser().getUserName().equals(admin)) {
            throw new BadRequestException("You cannot disable yourself!");
        }

        for(AssignmentEntity assignment : userDetail.getAssignmentTos()) {
            if(assignment.getState().equals(AssignmentState.WAITING_FOR_ACCEPTANCE)
                    || assignment.getState().equals(AssignmentState.ACCEPTED)
                    || assignment.getState().equals(AssignmentState.WAITING_FOR_RETURNING)) {
                throw new ConflictException(DISABLE_CONFLICT);
            }
        }

        if(userDetail.getAssignmentTos().size() > 0
                || userDetail.getAssignmentsBys().size() > 0
                || userDetail.getRequestAssignBys().size() > 0
                || userDetail.getRequestReturnBys().size() > 0
                || userDetail.getAcceptBys().size() > 0
                || userDetail.getConversationUser1s().size() > 0
                || userDetail.getConversationUser2s().size() > 0) {
            return ResponseEntity.ok(true); // if status code is 200 then disable user
        } else {
            return ResponseEntity.accepted().body(true); // if status code is 202 then delete user
        }
    }

    @Override
    public Boolean disableUser(String staffCode, String admin) {
        UserEntity userEntity = userRepository.findByStaffCode(staffCode)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        if(userEntity.getUserName().equals(admin)) {
            throw new BadRequestException("You cannot disable yourself!");
        }

        // admin cannot disable user when user has assignment in WAITING_FOR_ACCEPTANCE or ACCEPTED state
        for(AssignmentEntity assignment : userEntity.getUserDetail().getAssignmentTos()) {
            if(assignment.getState().equals(AssignmentState.WAITING_FOR_ACCEPTANCE)
                    || assignment.getState().equals(AssignmentState.ACCEPTED)
                    || assignment.getState().equals(AssignmentState.WAITING_FOR_RETURNING)) {
                throw new ConflictException(DISABLE_CONFLICT);
            }
        }

        if (userEntity.getUserDetail().getAssignmentTos().size() > 0
                || userEntity.getUserDetail().getAssignmentsBys().size() > 0
                || userEntity.getUserDetail().getRequestAssignBys().size() > 0
                || userEntity.getUserDetail().getRequestReturnBys().size() > 0
                || userEntity.getUserDetail().getAcceptBys().size() > 0
                || userEntity.getUserDetail().getConversationUser1s().size() > 0
                || userEntity.getUserDetail().getConversationUser2s().size() > 0) {
            userEntity.getUserDetail().setState(UserState.DISABLED);
            userRepository.save(userEntity);
        } else {
            userRepository.delete(userEntity);
        }
        return true;
    }

    @Override
    public UserDTO getProfile(String username) {
        UserEntity user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        return new UserDTO(user);
    }

    @Override
    public List<UserDTO> getAdmins() {
        return userRepository.findAllAdmin().stream().map(UserDTO::new).collect(Collectors.toList());
    }

    // check age >=18
    private boolean checkAge(LocalDate dOB, LocalDate joinDate) {
        Period period = Period.between(dOB, joinDate);
        return period.getYears() >= 18 ? true : false;
    }

    private final String USER_NOT_FOUND = "User is not found.";
    private final String DISABLE_CONFLICT = "There are valid assignments belonging to this user. Please close all assignments before disabling user.";

}
