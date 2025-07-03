package com.paymybuddy.app.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(name = "transaction")
public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;
	
	
	@ManyToOne(fetch = FetchType.EAGER,
				cascade = CascadeType.ALL)
	@JoinColumn(name = "sender_id")
	private User userSender;
	
	
	@ManyToOne(fetch = FetchType.EAGER,
				cascade = CascadeType.ALL)
	@JoinColumn(name = "receiver_id")
	private User userReceiver;
	
	
	private String description;
	
	private Double amount;
	
	
	
	
}
