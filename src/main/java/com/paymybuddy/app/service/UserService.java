package com.paymybuddy.app.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.paymybuddy.app.dto.UserUpdateDto;
import com.paymybuddy.app.exception.UserAlreadyExistException;
import com.paymybuddy.app.exception.UserAppendConnectionError;
import com.paymybuddy.app.exception.UserNotFoundException;
import com.paymybuddy.app.model.User;
import com.paymybuddy.app.repository.UserRepository;

import jakarta.transaction.Transactional;


@Service
public class UserService {
    
	private static final Logger LOGGER =  LogManager.getLogger();
		
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	SecurityService securityService;
		
	public Optional<User> getUserById(Integer id){
		return userRepository.findById(id);
	}
	
	public Optional<User> getUserByEmail(String email){
		return userRepository.findByEmail(email);
	}
	
	public User getCurrentUser() {
		return securityService.getCurrentUser();
	}
	
	public void refreshAuthentification() {
		securityService.refreshAuthentification();
	}
	
		
	public User createUser(User user) {
		if (userRepository.existsById(user.getId())){
			throw new UserAlreadyExistException("The user with the email " +  user.getEmail() + " already exist");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
		
	}
	
	public User updateUser(UserUpdateDto userUpdateDto) {
		User userConnected = getCurrentUser();
		String oldEmail = userConnected.getEmail();
		userConnected.setEmail(userUpdateDto.getEmail());
		userConnected.setUsername(userUpdateDto.getUsername());
		
		String newPassword = userUpdateDto.getNewPassword();
		String oldPassword = userUpdateDto.getOldPassword();
		//update password only if the length of the old or the new is > 0
		if (newPassword.length() > 0 || oldPassword.length() > 0) {
			if (!passwordEncoder.matches(oldPassword, userConnected.getPassword())) {
				throw new IllegalArgumentException("The old password is not correct");
			}
			userConnected.setPassword(passwordEncoder.encode(userUpdateDto.getNewPassword()));
		}
				
		//refresh the authentification for take the new email as username authentificate
		if (oldEmail != userUpdateDto.getEmail()) {
			refreshAuthentification();
		}
		
		return userRepository.save(userConnected);
		
		
		
		
	}
	
	
	public List<String> getEmailFromConnectionUser(List<User> connectionUser){
		List<Integer> connectionUserId = connectionUser.stream()
														.map(user -> user.getId())
														.toList();
		LOGGER.info("liste id user : " + connectionUserId);
		Iterable<User> users = userRepository.findAllById(connectionUserId);
		List<String> emails =  StreamSupport.stream(users.spliterator(), false)
									.map(user -> user.getEmail())
									.collect(Collectors.toList());
		return emails;
	}
		
	@Transactional	
	public User appendConnectionUser(String email) {
		User currentUser = securityService.getCurrentUser();
		User userAppend = getUserByEmail(email)
								.orElseThrow(() -> new UserNotFoundException("The user with the email " +  email + " is not found"));
		
		//Hibernate.initialize(currentUser.getConnectionUser()); // Force l'initialisation
		// Force l'initialisation en accédant à la collection
    
		//currentUser.getConnectionUser().forEach(connection -> {});
		if (currentUser.getConnectionUser().contains(userAppend)) {
			LOGGER.error("User {} is already a connection of {}", userAppend.getEmail(), currentUser.getEmail());
			throw new UserAppendConnectionError("The user with the mail " + 
												currentUser.getEmail() + 
												" already have the user " + 
												userAppend.getEmail() + 
												" in his connection list");
		}
		try {
			currentUser.addConnectionUser(userAppend);
			LOGGER.info("Connection successfully added  {} -> {}", userAppend.getEmail(), currentUser.getEmail());
			return userRepository.save(currentUser);
		}
		catch (Exception e) {
			LOGGER.error("emailUser {}", currentUser.getEmail(), e);
			throw new UserAppendConnectionError("Fail to append the user " +
												userAppend.getEmail() +
												" to the connection list of" +  
												currentUser.getEmail());
		}
	
	}
	
	public User deleteConnectionUser(User user, User userToDelete) {
		
		Optional<User> userOpt = userRepository.findById(user.getId());
		if (userOpt.isPresent()){
			User currentUser = userOpt.get();
			currentUser.removeConnectionUser(userToDelete);
			return userRepository.save(user);
		}
		else {
			throw new UserNotFoundException("The user with the email " +  user.getEmail() + " is not found");
		}
		
	}
}
