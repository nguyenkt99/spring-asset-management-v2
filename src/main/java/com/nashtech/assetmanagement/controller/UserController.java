package com.nashtech.assetmanagement.controller;

import com.nashtech.assetmanagement.dto.UserDTO;
import com.nashtech.assetmanagement.security.services.UserDetailsImpl;
import com.nashtech.assetmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/admins")
    public ResponseEntity<List<UserDTO>> getAdmins() {
        List<UserDTO> userDTOS = userService.getAdmins();
        return new ResponseEntity<>(userDTOS, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> getAll(Authentication authentication) {
        List<UserDTO> userDTOS = userService.retrieveUsers(authentication.getName());
        return new ResponseEntity<>(userDTOS, HttpStatus.OK);
    }

    @GetMapping("/{staffCode}")
    public ResponseEntity<UserDTO> getUserByStaffCode(@PathVariable("staffCode") String staffCode, Authentication authentication) {
        UserDTO userDTO = userService.getUserByStaffCode(staffCode, authentication.getName());
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UserDTO userDto = userService.getProfile(userDetails.getUsername());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<UserDTO> addUser(Authentication authentication, @Valid @RequestBody UserDTO userDto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return new ResponseEntity<>(userService.saveUser(userDto, userDetails.getUsername()), HttpStatus.CREATED);
    }

    @PutMapping("/{staffCode}")
    public ResponseEntity<UserDTO> editUser(@PathVariable("staffCode") String staffCode, @RequestBody UserDTO userDto) {
        userDto.setStaffCode(staffCode);
        UserDTO updateUser = userService.updateUser(userDto);
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
