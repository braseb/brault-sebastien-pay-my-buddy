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
	
	public void refreshAuthentification(String email) {
		securityService.refreshAuthentification(email);
	}
	
		
	public User createUser(User user) {
		LOGGER.info("Create user service");
		LOGGER.info(user.getEmail());
		if (userRepository.existsByEmail(user.getEmail())){
			UserAlreadyExistException userAlreadyExistException = new UserAlreadyExistException("The user with the email " +  user.getEmail() + " already exist");
			LOGGER.error("The user alreadyExist", userAlreadyExistException);
			throw userAlreadyExistException;
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
		
	}
	
	private void checkUserAlreadyExist(String email) {
	  //check if the new email user is not already take by another user
	    if (userRepository.existsByEmail(email)) {
	        UserAlreadyExistException userAlreadyExistException = new UserAlreadyExistException("The user with the email " +  email + " already exist");
            LOGGER.error("The user alreadyExist", userAlreadyExistException);
            throw userAlreadyExistException;
            
	    }
	}
	
	private void verifyOldPassword(String oldPassword, String currentHashedPassword) {
	    if (!passwordEncoder.matches(oldPassword, currentHashedPassword)) {
	        throw new IllegalArgumentException("The old password is not correct");
	    }
	}
	
	private void updatePassword(User user, String oldPassword, String newPassword) {
	  //update password only if the length of the old or the new is > 0
	    if (newPassword != null && oldPassword != null) {
	        if (!newPassword.isEmpty() && !oldPassword.isEmpty()) {
	            verifyOldPassword(oldPassword, user.getPassword());
	            user.setPassword(passwordEncoder.encode(newPassword));
	        }
	    }
	}
		
	public User updateUser(UserUpdateDto userUpdateDto) {
	    
	    LOGGER.info("Start update the profil"); 
	    User userConnected = getCurrentUser();
	    String newEmail = userUpdateDto.getEmail();
	    String oldEmail = userConnected.getEmail();
	    
	    if (!newEmail.equals(oldEmail)){
           checkUserAlreadyExist(newEmail);
        }
	    	    
		userConnected.setEmail(userUpdateDto.getEmail());
		userConnected.setUsername(userUpdateDto.getUsername());
		
		String newPassword = userUpdateDto.getNewPassword();
		String oldPassword = userUpdateDto.getOldPassword();
		
		updatePassword(userConnected, oldPassword, newPassword);
		
		
		User newUSer = userRepository.save(userConnected);
		
        //refresh the authentification for take the new email as username authentificate
        if (oldEmail != userUpdateDto.getEmail()) {
            refreshAuthentification(userUpdateDto.getEmail());
            LOGGER.info("Authentification refresh");
        }
        LOGGER.info("Profil update with success");
        return newUSer;
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
	public User appendConnectionUser(String email){
		User currentUser = securityService.getCurrentUser();
		User userAppend = getUserByEmail(email)
								.orElseThrow(() -> {
								                        LOGGER.error("User {} is not found", email);
								                        return new UserNotFoundException("The user with the email " +  email + " is not found");});
				
		if (currentUser.getEmail().equals(email)){
		    LOGGER.error("User {} could not append a connection with him", userAppend.getEmail());
		    throw new UserAppendConnectionError("User " + currentUser.getEmail() + " could not append a connection with him");
		}
		
		if (currentUser.getConnectionUser().contains(userAppend)) {
			LOGGER.error("User {} is already a connection of {}", userAppend.getEmail(), currentUser.getEmail());
			throw new UserAppendConnectionError("The user with the mail " + 
												currentUser.getEmail() + 
												" already have the user " + 
												userAppend.getEmail() + 
												" in his connection list");
		}
		
		try {
			if (currentUser.addConnectionUser(userAppend)) {
				LOGGER.info("Connection successfully added  {} -> {}", userAppend.getEmail(), currentUser.getEmail());
				return userRepository.save(currentUser);
			}
			else {
				LOGGER.info("Connection already exist  {} -> {}", userAppend.getEmail(), currentUser.getEmail());
				return currentUser;
			}
		} catch (RuntimeException e) {
			LOGGER.error("emailUser {}", currentUser.getEmail());
			throw new UserAppendConnectionError("Fail to append the user " +
												userAppend.getEmail() +
												" to the connection list of" +  
												currentUser.getEmail());
			
		}
		
	}
	
	public Integer updateCapital(String email, Double debit) {
	    return userRepository.updateCapital(email, debit);
	}
	
	public User deleteConnectionUser(User user, User userToDelete) {
		
		Optional<User> userOpt = userRepository.findById(user.getId());
		if (userOpt.isPresent()){
			User currentUser = userOpt.get();
			currentUser.removeConnectionUser(userToDelete);
			return userRepository.save(user);
		} else {
			throw new UserNotFoundException("The user with the email " +  user.getEmail() + " is not found");
		}
	}
}
