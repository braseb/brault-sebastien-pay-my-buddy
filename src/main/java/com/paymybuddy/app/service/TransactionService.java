package com.paymybuddy.app.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import com.paymybuddy.app.dto.TransactionFormDto;
import com.paymybuddy.app.exception.InsufficientAccountBalance;
import com.paymybuddy.app.model.Transaction;
import com.paymybuddy.app.model.User;
import com.paymybuddy.app.projection.TransactionProjection;
import com.paymybuddy.app.repository.TransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {
	
	private static final Logger LOGGER =  LogManager.getLogger();
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private UserService userService;
	
	@Transactional
	public List<TransactionProjection> getTransactionByUserId(Integer id) {
		List<TransactionProjection> transactionsProjection = transactionRepository.findByUserSenderId(id);
		LOGGER.info("list of transactions " + transactionsProjection);
				
		return transactionsProjection;
		
	}
	
	private void controlAccountBalance(Double accountBalance, Double amount) throws InsufficientAuthenticationException  {
	    if (amount > accountBalance) {
	        throw new InsufficientAccountBalance("Insufficient account balance for do the transaction");
	    }
	}
	
	@Transactional
	public Transaction saveTransaction(TransactionFormDto transactionFormDto) {
		User user = userService.getCurrentUser();
				
		User userReceiver = userService.getUserByEmail(transactionFormDto.getEmail())
										.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		LOGGER.info("user sender : {}", userReceiver.getEmail());
		LOGGER.info("user receiver : {}", user.getEmail());
		Transaction transaction = new Transaction();
		transaction.setUserReceiver(userReceiver);
		transaction.setUserSender(user);
		transaction.setAmount(transactionFormDto.getAmount());
		transaction.setDescription(transactionFormDto.getDescription());
		
		LOGGER.info("montant transaction : {}", transaction.getAmount());
		// Debit sender
	    Double userAccountBalance = user.getAccountBalance();
	    
	    controlAccountBalance(userAccountBalance, transactionFormDto.getAmount());
		user.setAccountBalance(userAccountBalance - transactionFormDto.getAmount());

	    //Credit receiver
	    userReceiver.setAccountBalance(userReceiver.getAccountBalance() + transactionFormDto.getAmount());
		return transactionRepository.save(transaction);
	}
	
	
}

