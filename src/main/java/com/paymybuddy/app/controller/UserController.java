package com.paymybuddy.app.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	public String getProfilePage() {
		return "Profile page";
	}
	
	@Transactional
	public String updateUser(User user) {
		return "update user";
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
