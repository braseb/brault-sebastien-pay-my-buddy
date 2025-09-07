package com.paymybuddy.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.paymybuddy.app")
public class PayMyBuddyApplication {
	
	
	
	public static void main(String[] args) {
		SpringApplication.run(PayMyBuddyApplication.class, args);
	}

	

}
