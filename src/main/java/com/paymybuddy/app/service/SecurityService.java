package com.paymybuddy.app.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.paymybuddy.app.exception.UserNotFoundException;
import com.paymybuddy.app.model.User;
import com.paymybuddy.app.repository.UserRepository;

@Service
public class SecurityService {
	@Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    public void refreshAuthentification() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = getCurrentUser();

        UserDetails updatedUserDetails = customUserDetailsService.loadUserByUsername(user.getEmail());

        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                updatedUserDetails, authentication.getCredentials(), updatedUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(newAuth);
       
    }
    
    public User getCurrentUser() {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    if (auth != null && auth.isAuthenticated()) {
	        String email = auth.getName();
	        return userRepository.findByEmail(email)
	                .orElseThrow(() -> new UserNotFoundException("The user with the email " +  email + " is not found"));
	    }

	    throw new RuntimeException("No user connected");
	}
}
