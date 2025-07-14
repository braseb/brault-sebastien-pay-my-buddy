package com.paymybuddy.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.app.model.Transaction;
import com.paymybuddy.app.projection.TransactionProjection;
import com.paymybuddy.app.repository.TransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Transactional
	public List<TransactionProjection> getTransactionByUserId(Integer id) {
		List<TransactionProjection> transactions = transactionRepository.findByUserSenderId(id);

		return transactions;
		/*return transactions.stream()
			.map(TransactionDto::new)
			.collect(Collectors.toList());*/
	}
	
	public Transaction saveTransaction(Transaction transaction) {
		return transactionRepository.save(transaction);
	}
}

