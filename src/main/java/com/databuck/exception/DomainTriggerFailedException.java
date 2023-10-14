package com.databuck.exception;

public class DomainTriggerFailedException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DomainTriggerFailedException(String message) {
		super(message);
	}
}
