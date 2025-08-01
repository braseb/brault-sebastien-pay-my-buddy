package com.paymybuddy.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.paymybuddy.app.model.Transaction;
import com.paymybuddy.app.projection.TransactionProjection;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
	
	@Query(value = "SELECT us.username, t.description, t.amount"
					+ " FROM transaction t"
					+ " INNER JOIN users u ON u.id = t.sender_id"
					+ " INNER JOIN users us ON us.id = t.receiver_id"
					+ " WHERE u.id = :id"
					, nativeQuery = true)
	
	public List<TransactionProjection> findByUserSenderId(@Param("id") Integer id);
}
