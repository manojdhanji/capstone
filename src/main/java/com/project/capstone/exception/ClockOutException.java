package com.project.capstone.exception;

public class ClockOutException extends RuntimeException {

	static final long serialVersionUID = -4915819806452853169L;

	/**
	 * 
	 */
	public ClockOutException() {
		super();
	}

	/**
	 * @param message
	 */
	public ClockOutException(String message) {
		super(message);
	}

	/**
	 * @param throwable
	 */
	public ClockOutException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * @param message
	 * @param throwable
	 */
	public ClockOutException(String message, Throwable throwable) {
		super(message, throwable);
	}

	/**
	 * @param message
	 * @param throwable
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ClockOutException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}
}
