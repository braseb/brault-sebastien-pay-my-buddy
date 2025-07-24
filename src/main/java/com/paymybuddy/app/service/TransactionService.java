package com.paymybuddy.app.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.app.dto.TransactionDto;
import com.paymybuddy.app.model.Transaction;
import com.paymybuddy.app.projection.TransactionProjection;
import com.paymybuddy.app.repository.TransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {
	
	private static final Logger LOGGER =  LogManager.getLogger();
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Transactional
	public List<TransactionDto> getTransactionByUserId(Integer id) {
		List<TransactionProjection> transactionsProjection = transactionRepository.findByUserSenderId(id);
		LOGGER.info("list of transactions " + transactionsProjection);
		
		List<TransactionDto> transactions = transactionsProjection.stream()
	            .map(p -> new TransactionDto(p.getUsername(), p.getDescription(), p.getAmount()))
	            .toList();
		
		return transactions;
		
	}
	
	public Transaction saveTransaction(Transaction transaction) {
		return transactionRepository.save(transaction);
	}
}

