package com.paymybuddy.app.exception;

public class UserNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = -8797260362650365031L;

	public UserNotFoundException(String message) {
		super(message);
	}

}
