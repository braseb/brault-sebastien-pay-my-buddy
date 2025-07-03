package com.paymybuddy.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.app.model.User;
import com.paymybuddy.app.repository.UserRepository;


@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	public Optional<User> getUserById(Integer id){
		return userRepository.findById(id);
	}
	
	public List<User> getUserWithAmountLessThan(Double amount){
		return userRepository.findBytransactionsSendAmountLessThan(amount);
	}
}
