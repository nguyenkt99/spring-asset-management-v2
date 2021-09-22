package com.nashtech.AssetManagement_backend.controller;

import com.nashtech.AssetManagement_backend.dto.UserDto;
import com.nashtech.AssetManagement_backend.entity.LocationEntity;
import com.nashtech.AssetManagement_backend.security.services.UserDetailsImpl;
import com.nashtech.AssetManagement_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<List<UserDto>> getAll (Authentication authentication) {
        LocationEntity location = userService.getLocationByUserName(authentication.getName());
        List<UserDto> userDtos = userService.retrieveUsers(location);

        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

    @GetMapping("/{staffCode}")
    public ResponseEntity<UserDto> getUserByStaffCode(Authentication authentication, @PathVariable("staffCode") String staffCode) {
        LocationEntity location = userService.getLocationByUserName(authentication.getName());
        UserDto userDto = userService.getUserByStaffCode(staffCode, location);

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UserDto userDto = userService.getProfile(userDetails.getUsername());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<UserDto> addUser(Authentication authentication, @Valid @RequestBody UserDto userDto) throws ParseException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return new ResponseEntity<>(userService.saveUser(userDto, userDetails.getUsername()), HttpStatus.CREATED);
    }

    @PutMapping("/{staffCode}")
    public ResponseEntity<UserDto> editUser(@PathVariable("staffCode") String staffCode, @RequestBody UserDto userDto) {
        userDto.setStaffCode(staffCode);
        UserDto updateUser = userService.updateUser(userDto);
        return new ResponseEntity<>(updateUser, HttpStatus.OK);
    }

    @GetMapping("/disable/{staffCode}")
    public ResponseEntity<Boolean> canDisableUser(@PathVariable("staffCode") String staffCode,Authentication authentication){
        return userService.canDisableUser(staffCode,authentication.getName());
    }

    @PutMapping("/disable/{staffCode}")
    public ResponseEntity<Boolean> disableUser(@PathVariable("staffCode") String staffCode, Authentication authentication){
        return ResponseEntity.ok().body(userService.disableUser(staffCode, authentication.getName()));
    }

}
