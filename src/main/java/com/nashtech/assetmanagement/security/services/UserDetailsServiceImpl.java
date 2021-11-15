package com.nashtech.assetmanagement.security.services;

import com.nashtech.assetmanagement.entity.UserEntity;
import com.nashtech.assetmanagement.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByUserName(username).orElseThrow(
                () -> new UsernameNotFoundException("User Not Found with -> username: " + username));

        return UserDetailsImpl.build(user);
    }
}
