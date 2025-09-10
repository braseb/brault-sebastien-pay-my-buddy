package com.paymybuddy.app.configuration;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CustomControllerAdvice {
	
		
	@ModelAttribute(value = "activePage")
	public String activePage(HttpServletRequest httpServletRequest) {
		return httpServletRequest.getRequestURI();
	}
	
	
}
