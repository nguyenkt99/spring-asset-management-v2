package com.nashtech.AssetManagement_backend.security.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.nashtech.AssetManagement_backend.entity.UserState;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nashtech.AssetManagement_backend.entity.UsersEntity;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private String staffCode;

    private String username;

    @JsonIgnore
    private String password;

    private boolean isFirstLogin;

    private UserState state;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String staffCode, String username, String password, boolean isFirstLogin, UserState state,
                Collection<? extends GrantedAuthority> authorities) {
        this.staffCode = staffCode;
        this.username = username;
        this.password = password;
        this.isFirstLogin = isFirstLogin;
        this.state = state;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(UsersEntity user) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getName().name()));

        return new UserDetailsImpl(
            user.getStaffCode(),
            user.getUserName(),
            user.getPassword(),
            user.isFirstLogin(),
            user.getUserDetail().getState(),
            authorities); //authorities
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getStaffCode() {
        return this.staffCode;
    }

    public Boolean isFirstLogin() {
        return this.isFirstLogin;
    }

    public UserState getState() {
        return state;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(staffCode, user.staffCode);
    }

}