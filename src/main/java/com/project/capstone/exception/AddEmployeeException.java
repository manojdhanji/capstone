package com.project.capstone.exception;

public class AddEmployeeException extends RuntimeException {

	static final long serialVersionUID = -2570343973687398598L;
	/**
	 * 
	 */
	public AddEmployeeException() {
		super();
	}

	/**
	 * @param message
	 */
	public AddEmployeeException(String message) {
		super(message);
	}

	/**
	 * @param throwable
	 */
	public AddEmployeeException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * @param message
	 * @param throwable
	 */
	public AddEmployeeException(String message, Throwable throwable) {
		super(message, throwable);
	}

	/**
	 * @param message
	 * @param throwable
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public AddEmployeeException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}
}
