package com.paymybuddy.app.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.app.dto.UserDto;
import com.paymybuddy.app.exception.UserNotFoundException;
import com.paymybuddy.app.model.User;
import com.paymybuddy.app.service.CustomUserDetailsService;
import com.paymybuddy.app.service.UserService;
import jakarta.transaction.Transactional;

@Controller
//@RestController
public class UserController {

    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
	
	private static final Logger LOGGER =  LogManager.getLogger();

    UserController(UserService userService, CustomUserDetailsService customUserDetailsService) {
        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
    }
	
		
	@GetMapping("/login")
	public String login() {
		return "login";
	}
		
	/*@PostMapping("/login")
	public String postLoging(@RequestParam String email, @RequestParam String password) {
		LOGGER.info("email : {}", email);
		LOGGER.info("password : {}", password);
		return "login";
	}*/
	
	@GetMapping("/profile")
	public String getProfile(Model model) {
		LOGGER.info("get profile user");
		User user = customUserDetailsService.getCurrentUser();
		model.addAttribute("user", user);
		return "profile";
	}
	
	@PutMapping("/profile")
	public String updateProfile(Model model, @ModelAttribute UserDto userDto) {
		LOGGER.info("update profile user");
		User userConnected = customUserDetailsService.getCurrentUser();
		String oldEmail = userConnected.getEmail();
				
		userConnected.setEmail(userDto.getEmail());
		if (userDto.getPassword().length() > 0) {
			userConnected.setPassword(userDto.getPassword());
		}
		userConnected.setUsername(userDto.getUsername());
				
		userService.updateUser(userConnected);
		if (oldEmail != userDto.getEmail()) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails updatedUserDetails = customUserDetailsService.loadUserByUsername(userConnected.getEmail());

			Authentication newAuth = new UsernamePasswordAuthenticationToken(
			    updatedUserDetails, authentication.getCredentials(), updatedUserDetails.getAuthorities());

			SecurityContextHolder.getContext().setAuthentication(newAuth);
		}
		
		
		return "redirect:/profile";
		
	}
	
	@GetMapping("/register")
	public String  getCreateProfile() {
		return "register";
	}
	
	@PostMapping("/register")
	public String createProfil(@ModelAttribute UserDto userDto) {
		User user = new User();
		user.setUsername(userDto.getUsername());
		user.setEmail(userDto.getEmail());
		user.setPassword(userDto.getPassword());
		
		userService.createUser(user);
		return "redirect:/login";
	}
	
	@GetMapping("/connection")
	public String connectionUser() {
		return "connection";
	}
	
	@PostMapping("/add_user_connection")
	public String addConnectionUser(@RequestParam(required = true) String email) {
		LOGGER.info("append user connection");
		User user = customUserDetailsService.getCurrentUser();
		User userConnection = userService.getUserByEmail(email)
								.orElseThrow(() -> new UserNotFoundException("user not found"));
		userService.appendConnectionUser(user, userConnection);
		return "connection";
	}
	
	
}
