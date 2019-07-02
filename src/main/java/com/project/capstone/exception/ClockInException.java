package com.project.capstone.exception;

public class ClockInException extends RuntimeException {
	static final long serialVersionUID = -4179992972273337500L;

	/**
	 * 
	 */
	public ClockInException() {
		super();
	}

	/**
	 * @param message
	 */
	public ClockInException(String message) {
		super(message);
	}

	/**
	 * @param throwable
	 */
	public ClockInException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * @param message
	 * @param throwable
	 */
	public ClockInException(String message, Throwable throwable) {
		super(message, throwable);
	}

	/**
	 * @param message
	 * @param throwable
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ClockInException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}
}
