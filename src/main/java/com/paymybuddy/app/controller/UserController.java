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
//@Validated
//@RestController
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
	
	/*@GetMapping("/connection")
	public String connectionUser() {
		return "connection";
	}
	
	@PostMapping("/add_user_connection")
	public String addConnectionUser(@RequestParam(required = true)
									@NotBlank(message = "Email is required")
    								@Email(message = "L'Email is not valid")
									String email, 
									RedirectAttributes redirectAttributes) {
		LOGGER.info("append user connection");
		try {
			userService.appendConnectionUser(email);
			redirectAttributes.addFlashAttribute("successMessage", "Connection append successfull !");
			LOGGER.info("User with the email {} is append with success", email);
			
		} 
		catch (UserAppendConnectionError e) {
			redirectAttributes.addFlashAttribute("emailError", e.getMessage());
			LOGGER.error("The User with the email {} already exist", email, e);
		}
		
		catch (RuntimeException ex) {
			throw ex;
		}
		
		return "redirect:/connection";
	}*/
	
	 /*@ExceptionHandler(ConstraintViolationException.class)
	    public String handleValidationError(ConstraintViolationException ex, RedirectAttributes redirectAttributes) {
	     redirectAttributes.addFlashAttribute("emailError",
	                ex.getConstraintViolations().iterator().next().getMessage());
	        LOGGER.error("The email is not valid", ex);
	        return "redirect:/connection";
	    }*/
	 
	 /*@ExceptionHandler(UserNotFoundException.class)
	 public String userNotFoundError(UserNotFoundException ex, RedirectAttributes redirectAttributes) {
		 redirectAttributes.addFlashAttribute("globalError",
	                ex.getMessage());
		 LOGGER.error("User not found", ex);   
		 return "redirect:/connection";
	 }*/
	 
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
