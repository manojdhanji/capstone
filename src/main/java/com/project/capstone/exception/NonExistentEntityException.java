package com.project.capstone.exception;

public class NonExistentEntityException extends RuntimeException{
	static final long serialVersionUID = 2229212828591038919L;

	/**
	 * 
	 */
	public NonExistentEntityException() {
		super();
	}

	/**
	 * @param message
	 */
	public NonExistentEntityException(String message) {
		super(message);
	}

	/**
	 * @param throwable
	 */
	public NonExistentEntityException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * @param message
	 * @param throwable
	 */
	public NonExistentEntityException(String message, Throwable throwable) {
		super(message, throwable);
	}

	/**
	 * @param message
	 * @param throwable
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public NonExistentEntityException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}
}
