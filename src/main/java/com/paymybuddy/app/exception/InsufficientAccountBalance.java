package com.paymybuddy.app.exception;

public class InsufficientAccountBalance extends RuntimeException {
	
	private static final long serialVersionUID = -8045414519446210848L;

    public InsufficientAccountBalance(String message) {
		super(message);
	}

}
