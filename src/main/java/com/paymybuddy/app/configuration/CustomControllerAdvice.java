package com.paymybuddy.app.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CustomControllerAdvice {
	
	private static final Logger LOGGER =  LogManager.getLogger();
	
	@ModelAttribute(value = "activePage")
	public String activePage(HttpServletRequest httpServletRequest) {
		return httpServletRequest.getRequestURI();
	}
	
	@ExceptionHandler(Exception.class) // attrape toutes les exceptions non gérées
    public String handleUnexpectedException(Exception ex, Model model) {
        model.addAttribute("errorMessage", "An unexpected error has occurred. Please try again later.");
        LOGGER.error("An unexpected error has occurred.", ex);
        return "error";
    }
}
