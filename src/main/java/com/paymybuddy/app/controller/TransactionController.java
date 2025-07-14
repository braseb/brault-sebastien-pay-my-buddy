package com.paymybuddy.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;



//@Controller
@RestController
public class TransactionController {
	@GetMapping("/transaction")
	
	public String getTransactionPage() {
		return "Transaction page";
	}
	
	@PutMapping("/create_transaction/")
	public String createTransaction() {
		return "create transaction";
	}
}
