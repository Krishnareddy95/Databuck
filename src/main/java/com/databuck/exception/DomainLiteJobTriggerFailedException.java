package com.databuck.exception;

public class DomainLiteJobTriggerFailedException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DomainLiteJobTriggerFailedException(String message) {
		super(message);
	}
}
