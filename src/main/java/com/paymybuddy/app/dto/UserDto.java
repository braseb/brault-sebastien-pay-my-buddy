package com.paymybuddy.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor


public class UserDto {
	
	@NotEmpty(message = "A username is required")
	String username;
	
	@Email(message = "A valid email address is required")
	@NotEmpty(message = "A valid email address is required")
	String email;
	
	@Size(min = 3, message = "The minimal lenght of the password is 5")
	String password;
	
	
	
	
}
