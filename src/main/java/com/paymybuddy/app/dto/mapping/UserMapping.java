package com.paymybuddy.app.dto.mapping;

import com.paymybuddy.app.dto.UserDto;
import com.paymybuddy.app.model.User;

public class UserMapping {
	public static User mapToUser(UserDto userCreate) {
		User user = new User();
		user.setEmail(userCreate.getEmail());
		user.setUsername(userCreate.getUsername());
		user.setPassword(userCreate.getPassword());
		
		
		return user;
	}
}
