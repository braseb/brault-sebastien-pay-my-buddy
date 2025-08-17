package com.paymybuddy.app.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paymybuddy.app.dto.TransactionFormDto;
import com.paymybuddy.app.model.Transaction;
import com.paymybuddy.app.model.User;
import com.paymybuddy.app.service.TransactionService;
import com.paymybuddy.app.service.UserService;

import jakarta.validation.Valid;



@Controller
//@RestController
public class TransactionController {
	
	private static final Logger LOGGER =  LogManager.getLogger();
	
	@Autowired
	TransactionService transactionService;
	
	@Autowired
	UserService userService;
		
	
	@GetMapping("/transaction")
	public String getTransactionPage(Model model) {
		User user = userService.getCurrentUser();
		List<String> listEmail = userService.getEmailFromConnectionUser(user.getConnectionUser());
		LOGGER.info("liste mails : " + listEmail);
		model.addAttribute("transactions", transactionService.getTransactionByUserId(user.getId()))
			.addAttribute("listEmail", listEmail)
			.addAttribute("transactionFormDto", new TransactionFormDto("", "", 0.0));
		
				
		
		return "transaction";
	}
		
	@PostMapping("/transaction")
	public String createTransaction(Model model,
									@Valid @ModelAttribute TransactionFormDto transactionFormDto,
									BindingResult bindingResult, 
									RedirectAttributes redirectAttributes){
		LOGGER.info("Create transaction");
		 
		if (bindingResult.hasErrors()){
			bindingResult.getFieldErrors().forEach(fe -> 
							LOGGER.error(fe.getField(), fe.getDefaultMessage()));
			User user = userService.getCurrentUser();
			List<String> listEmail = userService.getEmailFromConnectionUser(user.getConnectionUser());
			LOGGER.info("liste mails : " + listEmail);
			model.addAttribute("transactions", transactionService.getTransactionByUserId(user.getId()))
				.addAttribute("listEmail", listEmail);
			
			return "transaction";
		}
		transactionService.saveTransaction(transactionFormDto);
				
		return "redirect:/transaction";
	}
	
	/*@ExceptionHandler({MissingServletRequestParameterException.class, TypeMismatchException.class})
    public String handleMissingParams(Exception ex, RedirectAttributes redirectAttributes) {
		
        redirectAttributes.addFlashAttribute("globalError", "Tous les champs sont obligatoires et doivent être valides.");
		return "redirect:/transaction"; // Retourne la même page avec un message global
    }*/
}
