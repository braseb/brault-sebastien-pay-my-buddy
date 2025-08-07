package com.paymybuddy.app.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.paymybuddy.app.model.Transaction;
import com.paymybuddy.app.model.User;
import com.paymybuddy.app.service.TransactionService;
import com.paymybuddy.app.service.UserService;



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
		//List<TransactionDto> listTransations = transactionService.getTransactionByUserId(user.getId());
		model.addAttribute("transactions", transactionService.getTransactionByUserId(user.getId()))
				.addAttribute("listEmail", listEmail);
		//model.addAttribute("listEmail", listEmail);
				
		
		return "transaction";
	}
	
	
	
	@PostMapping("/transaction")
	public String createTransaction(Model model,  @RequestParam(required = true) String emailSelect,
													@RequestParam(defaultValue = "") String description,
													@RequestParam(required = true) Double amount) {
		LOGGER.info("Create transaction");
		User user = userService.getCurrentUser();
		
		User userReceiver = userService.getUserByEmail(emailSelect)
										.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		LOGGER.info("user sender : {}", userReceiver.getEmail());
		LOGGER.info("user receiver : {}", user.getEmail());
		Transaction transaction = new Transaction();
		transaction.setUserReceiver(userReceiver);
		transaction.setUserSender(user);
		transaction.setAmount(amount);
		transaction.setDescription(description);
		
		LOGGER.info("montant transaction : {}", transaction.getAmount());
		
		transactionService.saveTransaction(transaction);
				
		/*List<String> listEmail = userService.getEmailFromConnectionUser(user.getConnectionUser());
		List<TransactionDto> listTransations = transactionService.getTransactionByUserId(user.getId());
		model.addAttribute("transactions", listTransations)
		.addAttribute("listEmail", listEmail);*/
		return "redirect:/transaction";
	}
	
	@ExceptionHandler({MissingServletRequestParameterException.class, TypeMismatchException.class})
    public String handleMissingParams(Exception ex, RedirectAttributes redirectAttributes) {
		/*User user = customUserDetailsService.getCurrentUser();
		List<String> listEmail = userService.getEmailFromConnectionUser(user.getConnectionUser());
		LOGGER.info("liste mails : " + listEmail);
		List<TransactionDto> listTransations = transactionService.getTransactionByUserId(user.getId());
		model.addAttribute("transactions", listTransations)
				.addAttribute("listEmail", listEmail)
				.addAttribute("globalError", "Tous les champs sont obligatoires et doivent être valides.");*/
        redirectAttributes.addFlashAttribute("globalError", "Tous les champs sont obligatoires et doivent être valides.");
		return "redirect:/transaction"; // Retourne la même page avec un message global
    }
}
