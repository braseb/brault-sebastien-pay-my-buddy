package com.paymybuddy.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.app.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

	//public List<User> findBytransactionsSendAmountLessThan(Double cost);

	public Optional<User> findByEmail(String email);
	public boolean existsByEmail(String email);
	
	
}
