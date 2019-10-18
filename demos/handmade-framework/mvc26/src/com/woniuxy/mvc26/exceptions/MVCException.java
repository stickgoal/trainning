package com.woniuxy.mvc26.exceptions;

public class MVCException extends RuntimeException{

	public MVCException() {
		super();
	}

	public MVCException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MVCException(String message, Throwable cause) {
		super(message, cause);
	}

	public MVCException(String message) {
		super(message);
	}

	public MVCException(Throwable cause) {
		super(cause);
	}

}
