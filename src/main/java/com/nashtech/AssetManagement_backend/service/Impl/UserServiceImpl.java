package com.nashtech.AssetManagement_backend.service.Impl;

import com.nashtech.AssetManagement_backend.dto.UserDto;
import com.nashtech.AssetManagement_backend.entity.*;
import com.nashtech.AssetManagement_backend.exception.BadRequestException;
import com.nashtech.AssetManagement_backend.exception.ConflictException;
import com.nashtech.AssetManagement_backend.exception.InvalidInputException;
import com.nashtech.AssetManagement_backend.exception.ResourceNotFoundException;
import com.nashtech.AssetManagement_backend.handleException.NotFoundExecptionHandle;
import com.nashtech.AssetManagement_backend.repository.LocationRepository;
import com.nashtech.AssetManagement_backend.repository.RoleRepository;
import com.nashtech.AssetManagement_backend.repository.UserDetailRepository;
import com.nashtech.AssetManagement_backend.repository.UserRepository;
import com.nashtech.AssetManagement_backend.service.UserService;
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

    @Override
    public UsersEntity findByUserName(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new NotFoundExecptionHandle("Could not found user: " + username));
    }

    @Override
    public UserDetailEntity findByEmail(String email) {
        return userDetailRepository.findByEmail(email).orElseThrow(()-> new NotFoundExecptionHandle("Could not found user: " + email));
    }

    @Override
    public UserDto changePasswordAfterfirstLogin(String username, String passwordEncode) {
        UsersEntity existUser = findByUserName(username);
        existUser.setPassword(passwordEncode);
        existUser.setFirstLogin(false);

        try {
            UsersEntity user = userRepository.save(existUser);
            return new UserDto().toDto(user);
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
            return new UserDto().toDto(user);
        } catch (Exception e) {
            throw new BadRequestException("invalid Request");
        }
    }

    @Override
    public UserDto saveUser(UserDto userDto, String username) {
        LocationEntity location = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!")).getUserDetail().getLocation();
        UsersEntity usersEntity = userDto.toEntity(userDto);
        usersEntity.getUserDetail().setLocation(location);
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
        return new UserDto().toDto(userRepository.getByStaffCode(usersEntity.getStaffCode()));
    }

    @Override
    public List<UserDto> retrieveUsers(LocationEntity location) {
//        List<UsersEntity> usersEntities = userRepository.findAllByLocationAndState(location, UserState.Enable);
        List<UsersEntity> usersEntities = userRepository.findAllByUserDetail_Location(location);
        usersEntities = usersEntities.stream()
//                .sorted(Comparator.comparing(o -> (o.getUserDetail().getFirstName() + ' ' + o.getUserDetail().getLastName())))
                .sorted(Comparator.comparing(o -> (o.getStaffCode())))
                .collect(Collectors.toList());
        return new UserDto().toListDto(usersEntities);
    }


    @Override
    public UserDto getUserByStaffCode(String staffCode, LocationEntity location) throws ResourceNotFoundException {
        UsersEntity user = userRepository.findByStaffCodeAndUserDetail_Location(staffCode, location)
                .orElseThrow(() -> new ResourceNotFoundException("user not found for this staff code: " + staffCode));
        return new UserDto().toDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        UsersEntity existUser = userRepository.findByStaffCode(userDto.getStaffCode()).orElseThrow(
                () -> new ResourceNotFoundException("User not found for this staff code: " + userDto.getStaffCode()));

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

        RolesEntity rolesEntity = roleRepository.getByName(userDto.getType());
        existUser.setRole(rolesEntity);

        UsersEntity user = userRepository.save(existUser);
        return new UserDto().toDto(user);
    }

    public LocationEntity getLocationByUserName(String userName) {
        return userRepository.getByUserName(userName).getUserDetail().getLocation();
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

        if (usersEntity.getUserDetail().getAssignmentTos().size() > 0) {
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
        return new UserDto().toDto(user);
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
