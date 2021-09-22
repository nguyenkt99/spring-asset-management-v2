package com.nashtech.AssetManagement_backend.service.Impl;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.nashtech.AssetManagement_backend.dto.UserDto;
import com.nashtech.AssetManagement_backend.entity.UserDetailEntity;
import com.nashtech.AssetManagement_backend.entity.UserState;
import com.nashtech.AssetManagement_backend.entity.UsersEntity;
import com.nashtech.AssetManagement_backend.exception.ConflictException;
import com.nashtech.AssetManagement_backend.handleException.RuntimeExceptionHandle;
import com.nashtech.AssetManagement_backend.payload.request.LoginRequest;
import com.nashtech.AssetManagement_backend.payload.response.JwtResponse;
import com.nashtech.AssetManagement_backend.security.jwt.JwtUtils;
import com.nashtech.AssetManagement_backend.security.services.UserDetailsImpl;
import com.nashtech.AssetManagement_backend.service.AuthService;
import com.nashtech.AssetManagement_backend.service.OTPService;
import com.nashtech.AssetManagement_backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder encoder;
    private final UserService userService;
    private final JavaMailSender javaMailSender;
    @Autowired
    OTPService otpService;

    @Autowired
    AuthServiceImpl(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
                    PasswordEncoder encoder, UserService userService, JavaMailSender javaMailSender) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.encoder = encoder;
        this.userService = userService;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        // TODO, authenticate when login
        // Username, pass from client
        // com.nashtech.rookies.security.WebSecurityConfig.configure(org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder)
        // authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        // on this step, we tell to authenticationManager how we load data from database
        // and the password encoder

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // if go there, the user/password is correct
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // generate jwt to return to client
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if(userDetails.getState().equals(UserState.Disabled)) {
            throw new ConflictException("Account is disabled!");
        }

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getStaffCode(), userDetails.getUsername(),
                roles.get(0), userDetails.isFirstLogin()));
    }

    @Override
    public UserDto changePasswordAfterfirstLogin(String username, String password) {
        String passwordEncode = encoder.encode(password);
        return userService.changePasswordAfterfirstLogin(username, passwordEncode);
    }

    @Override
    public Boolean changepassword(String username, String oldPassword, String newPassword) {
        HttpStatus statusCode = null;
        try {
            ResponseEntity<?> result = authenticateUser(new LoginRequest(username, oldPassword));
            statusCode = result.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                String passwordEncode = encoder.encode(newPassword);
                if (userService.changePassword(username, passwordEncode) != null)
                    return true;
                return false;
            }
        } catch (Exception e) {
            new RuntimeExceptionHandle(e.toString());
        }
        return false;
    }

    @Override
    public Boolean getOTP(String email) {
        UserDetailEntity entity = userService.findByEmail(email);
        if (entity != null) {
            int OTP = otpService.generateOTP(entity.getUser().getUserName());
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("Your OTP is");
            msg.setText("Your OTP is " + OTP);
            javaMailSender.send(msg);
            return true;
        } else {
            return false;
        }
    }

    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    public static String getRandomPassword() {

        Random rnd = new Random();
        int number = rnd.nextInt(99999999);

        return String.format("%08d", number);
    }

    @Override
    public Boolean validOTP(String email, int OTP) {

        UserDetailEntity entity = userService.findByEmail(email);
        if (OTP >= 0) {
            int serverOtp = otpService.getOtp(entity.getUser().getUserName());
            if (serverOtp > 0) {
                if (OTP == serverOtp) {
                    otpService.clearOTP(entity.getUser().getUserName());
                    String pass=getRandomPassword();
                    SimpleMailMessage msg = new SimpleMailMessage();
                    msg.setTo(email);
                    msg.setSubject("Your password is");
                    msg.setText("Your password is "+pass);
                    try{
                        changePasswordAfterfirstLogin(entity.getUser().getUserName(),pass);
                        javaMailSender.send(msg);
                        return true;
                    }catch (Exception e)
                    {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
