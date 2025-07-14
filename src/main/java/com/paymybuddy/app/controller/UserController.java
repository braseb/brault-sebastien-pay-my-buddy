package com.paymybuddy.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paymybuddy.app.model.User;

import jakarta.transaction.Transactional;

//@Controller
@RestController
public class UserController {
	
	@GetMapping("/connection")
	public String getConnectionPage() {
		return "Connection page";
	}
	
	@GetMapping("/profile")
	public String getProfilePage() {
		return "Profile page";
	}
	
	@Transactional
	public String updateUser(User user) {
		return "update user";
	}
	
	@Transactional
	public String addConnectionUser(User user) {
		return "add connection user";
	}
}
