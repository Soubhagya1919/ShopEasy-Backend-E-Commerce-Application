package com.soubhagya.electronic.store.services.impl;

import com.soubhagya.electronic.store.exceptions.ResourceNotFoundException;
import com.soubhagya.electronic.store.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailService is an implementation of the UserDetailsService interface
 * that provides custom user lookup logic for authentication purposes.
 *
 * It utilizes the UserRepository to fetch detailed user information based on a
 * specified username, typically the user's email.
 *
 * If no user is found with the provided username, a ResourceNotFoundException is thrown.
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User not found with given username !!"));
    }
}
