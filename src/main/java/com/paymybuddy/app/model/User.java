package com.paymybuddy.app.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
@DynamicUpdate
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private int id;
	
	private String username;
	
	private String email;
	
	private String password;
	
	public User(String username, String email, String password){
		this.username = username;
		this.email=email;
		this.password=password;
	}
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_connection",
				joinColumns = @JoinColumn(name = "user_id"),
				inverseJoinColumns = @JoinColumn(name = "user_connection_id"),
				uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "user_connection_id"})
				)
	List<User> connectionUser = new ArrayList<User>();
		
	@OneToMany(fetch = FetchType.LAZY,
			mappedBy = "userReceiver")
	private List<Transaction> transactionsReceived;
	
	@OneToMany(fetch = FetchType.LAZY,
			mappedBy = "userSender")
	private List<Transaction> transactionsSend;
	
	public boolean addConnectionUser(User user) {
		if (!connectionUser.contains(user)) {
			connectionUser.add(user);
			return true;
		}
		return false;
		
		//user.getConnectionUser().add(this);
	}
	
	public void removeConnectionUser(User user) {
		//user.getConnectionUser().remove(this);
		connectionUser.remove(user);
	}
	
}
