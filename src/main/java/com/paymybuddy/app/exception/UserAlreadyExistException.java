package com.paymybuddy.app.exception;

public class UserAlreadyExistException extends RuntimeException {

	private static final long serialVersionUID = -7077405312662006615L;
	
	public UserAlreadyExistException(String message) {
		super(message);
	}

}
