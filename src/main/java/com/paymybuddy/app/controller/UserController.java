package com.paymybuddy.app.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.app.dto.UserDto;
import com.paymybuddy.app.dto.UserUpdateDto;
import com.paymybuddy.app.dto.mapping.UserMapping;
import com.paymybuddy.app.exception.UserAlreadyExistException;
import com.paymybuddy.app.model.User;
import com.paymybuddy.app.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@Controller
public class UserController {

    private final UserService userService;
    
	
	private static final Logger LOGGER =  LogManager.getLogger();

    UserController(UserService userService) {
        this.userService = userService;
        
    }
		
	@GetMapping("/login")
	public String login(@RequestParam(required = false) String error, Model model) {
		if (error != null) {
		    LOGGER.error("Email or password is invalid");
		   
		}
	    return "login";
	}
		
	@GetMapping("/profile")
	public String getProfile(Model model) {
		LOGGER.info("get profile user");
		User user = userService.getCurrentUser();
		UserUpdateDto dto = new UserUpdateDto(user.getUsername(), user.getEmail(), null, null);
	    model.addAttribute("userUpdateDto", dto);
		//model.addAttribute("user", user);
		return "profile";
	}
	
	@PutMapping("/profile")
	public String updateProfile(Model model, 
        				@Valid @ModelAttribute UserUpdateDto userUpdateDto, 
        				BindingResult bindingResult, 
        				RedirectAttributes redirectAttributes) {
		LOGGER.info("update profile user");
		if (bindingResult.hasErrors()) {
			bindingResult.getFieldErrors().forEach(fe -> 
								LOGGER.error(fe.getField(), fe.getDefaultMessage()));
		
			User user = userService.getCurrentUser();
			model.addAttribute("user", user);
			return "profile";
		}
				
		userService.updateUser(userUpdateDto);
		return "redirect:/profile";
	}
	
	@GetMapping("/register")
	public String  getCreateProfile(@ModelAttribute UserDto userDto) {
		return "register";
	}
	
	@PostMapping("/register")
	public String createProfil(@Valid @ModelAttribute UserDto userDto, 
								BindingResult bindingResult, 
								RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()){
			bindingResult.getFieldErrors().forEach(fe -> 
											LOGGER.error(fe.getField(), fe.getDefaultMessage()));
			return "register";
		}
		
		userService.createUser(UserMapping.mapToUser(userDto));
		redirectAttributes.addFlashAttribute("successCreate", "User create with success");
		return "redirect:/login";
		
	}
		 
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex,
                                        RedirectAttributes redirectAttributes,
                                        HttpServletRequest request) {
        redirectAttributes.addFlashAttribute("globalError", ex.getMessage());
        LOGGER.info("Illegal except");
        LOGGER.error(ex.getMessage(), ex);
        return "redirect:/profile";
    }
	
     @ExceptionHandler(UserAlreadyExistException.class)
     public String userNotFoundError(UserAlreadyExistException ex, 
                                     RedirectAttributes redirectAttributes,
                                     HttpServletRequest request) {
    	 redirectAttributes.addFlashAttribute("globalError",
                ex.getMessage());
     LOGGER.error("User already exist", ex);   
     String from = request.getParameter("from");
     if (from.equals("profile")) {
         return "redirect:/profile";
     }
     return "redirect:/register";
     }
    	
	
	
}
