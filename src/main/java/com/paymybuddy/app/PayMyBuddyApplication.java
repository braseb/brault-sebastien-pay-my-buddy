package com.paymybuddy.app;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.paymybuddy.app.model.Transaction;
import com.paymybuddy.app.model.User;
import com.paymybuddy.app.projection.TransactionProjection;
import com.paymybuddy.app.service.TransactionService;
import com.paymybuddy.app.service.UserService;


@SpringBootApplication
@ComponentScan(basePackages = "com.paymybuddy.app")
public class PayMyBuddyApplication implements CommandLineRunner {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private TransactionService transactionService;
	
	public static void main(String[] args) {
		SpringApplication.run(PayMyBuddyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		/*Optional<User> optUser = userService.getUserById(1);
		User user = optUser.get();
		System.out.println(user.getUsername());
				
		List<User> users = userService.getUserWithAmountLessThan(1100.0);
		users.forEach(userParse -> System.out.println(userParse.getUsername()));
		
		List<TransactionProjection> transactions = transactionService.getTransactionByUserId(2);
		System.out.println("transactions");
		transactions.forEach(transaction -> System.out.println(transaction.getUsername()));*/
		
		//insert transaction
		/*Transaction transaction = new Transaction();
		Optional<User> sender = userService.getUserById(1);
		Optional<User> receiver = userService.getUserById(2);
		if (sender.isPresent() && receiver.isPresent()) {
			transaction.setUserSender(sender.get());
			transaction.setUserReceiver(receiver.get());
			transaction.setDescription("test3");
			transaction.setAmount(900.0);
			transactionService.saveTransaction(transaction);
		}*/
		
		//add userConnection
		/*Optional<User> userConnectedOpt = userService.getUserByEmail("tata@tata.fr");
		Optional<User> userToAppend = userService.getUserByEmail("toto@toto.fr");
		if (userConnectedOpt.isPresent() && userToAppend.isPresent()) {
			userService.appendConnectionUser(userConnectedOpt.get(), userToAppend.get());
		}*/
				
		//get list of connectionUser
		
		
		
	}

}
