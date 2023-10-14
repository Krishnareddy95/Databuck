package com.databuck.exception;

public class ValidationTriggerFailedException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidationTriggerFailedException(String message) {
		super(message);
	}
}
