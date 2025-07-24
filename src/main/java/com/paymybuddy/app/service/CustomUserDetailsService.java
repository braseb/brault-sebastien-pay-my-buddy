package com.paymybuddy.app.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.paymybuddy.app.model.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	UserService userService;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
				
		User user = userService.getUserByEmail(email)
								.orElseThrow(() ->  new UsernameNotFoundException(String.format("User with email {%s} not found", email)));
					
		return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .build();
		//return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
		}
	
	public User getCurrentUser() {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    if (auth != null && auth.isAuthenticated()) {
	        String email = auth.getName(); // ou getPrincipal().getUsername()
	        System.out.println(email);
	        System.out.println(auth);
	        return userService.getUserByEmail(email)
	                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable en base"));
	    }

	    throw new RuntimeException("Utilisateur non connecté");
	}
	
}
