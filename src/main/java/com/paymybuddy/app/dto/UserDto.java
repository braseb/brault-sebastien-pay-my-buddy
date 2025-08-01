package com.paymybuddy.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
	String username;
	String email;
	String password;
	
	
}
