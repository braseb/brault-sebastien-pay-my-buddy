package com.paymybuddy.app;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.paymybuddy.app.model.User;
import com.paymybuddy.app.service.UserService;

@SpringBootApplication
public class PayMyBuddyApplication implements CommandLineRunner {

	@Autowired
	private UserService userService;
	
	public static void main(String[] args) {
		SpringApplication.run(PayMyBuddyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Optional<User> optUser = userService.getUserById(1);
		User user = optUser.get();
		System.out.println(user.getUsername());
		
		List<User> users = userService.getUserWithAmountLessThan(1100.0);
		users.forEach(userParse -> System.out.println(userParse.getUsername()));
		
	}

}
