package com.paymybuddy.app.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.app.dto.UserDto;
import com.paymybuddy.app.dto.mapping.UserMapping;
import com.paymybuddy.app.model.User;
import com.paymybuddy.app.service.UserService;
import jakarta.validation.Valid;

@Controller
//@RestController
public class UserController {

    private final UserService userService;
    
	
	private static final Logger LOGGER =  LogManager.getLogger();

    UserController(UserService userService) {
        this.userService = userService;
        
    }
	
		
	@GetMapping("/login")
	public String login(Model model) {
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
		User user = userService.getCurrentUser();
		model.addAttribute("user", user);
		return "profile";
	}
	
	@PutMapping("/profile")
	public String updateProfile(Model model, @ModelAttribute UserDto userDto) {
		LOGGER.info("update profile user");
		User userConnected = userService.getCurrentUser();
		String oldEmail = userConnected.getEmail();
				
		userConnected.setEmail(userDto.getEmail());
		if (userDto.getPassword().length() > 0) {
			userConnected.setPassword(userDto.getPassword());
		}
		userConnected.setUsername(userDto.getUsername());
				
		userService.updateUser(userConnected);
		if (oldEmail != userDto.getEmail()) {
			userService.refreshAuthentification();
		}
		
		
		return "redirect:/profile";
		
	}
	
	@GetMapping("/register")
	public String  getCreateProfile(@ModelAttribute UserDto userDto) {
		return "register";
	}
	
	@PostMapping("/register")
	public String createProfil(@Valid @ModelAttribute UserDto userDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()){
			return "register";
		}
		
		userService.createUser(UserMapping.mapToUser(userDto));
		redirectAttributes.addFlashAttribute("successCreate", "User create with success");
		return "redirect:/login";
	}
	
	@GetMapping("/connection")
	public String connectionUser() {
		return "connection";
	}
	
	@PostMapping("/add_user_connection")
	public String addConnectionUser(@RequestParam(required = true) String email) {
		LOGGER.info("append user connection");
		userService.appendConnectionUser(email);
		return "connection";
	}
	
	
}
