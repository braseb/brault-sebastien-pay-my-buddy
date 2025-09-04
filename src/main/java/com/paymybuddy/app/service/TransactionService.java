package com.paymybuddy.app.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.paymybuddy.app.dto.TransactionFormDto;
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
		//user.setCapital(user.getCapital() - transactionFormDto.getAmount());
		//userService.updateCapital(user.getEmail(), transactionFormDto.getAmount());
		
		// Debit sender
	    //userSender.setCapital(userSender.getCapital() - transactionFormDto.getAmount());

	    // Credit receiver
	    //userReceiver.setCapital(userReceiver.getCapital() + transactionFormDto.getAmount());
		return transactionRepository.save(transaction);
	}
}

