package com.paymybuddy.app.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.paymybuddy.app.exception.UserAppendConnectionError;
import com.paymybuddy.app.exception.UserNotFoundException;
import com.paymybuddy.app.service.UserService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Controller
@Validated
public class ConnectionController {

    private final UserService userService;
    
	
	private static final Logger LOGGER =  LogManager.getLogger();

    ConnectionController(UserService userService) {
        this.userService = userService;
        
    }
		
	@GetMapping("/connection")
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
	}
	
	 @ExceptionHandler(ConstraintViolationException.class)
	    public String handleValidationError(ConstraintViolationException ex, RedirectAttributes redirectAttributes) {
	     redirectAttributes.addFlashAttribute("emailError",
	                ex.getConstraintViolations().iterator().next().getMessage());
	        LOGGER.error("The email is not valid", ex);
	        return "redirect:/connection";
	    }
	 
	 @ExceptionHandler(UserNotFoundException.class)
	 public String userNotFoundError(UserNotFoundException ex, RedirectAttributes redirectAttributes) {
		 redirectAttributes.addFlashAttribute("emailError",
	                ex.getMessage());
		 LOGGER.error("User not found", ex);   
		 return "redirect:/connection";
	 }
	
	
	
}
