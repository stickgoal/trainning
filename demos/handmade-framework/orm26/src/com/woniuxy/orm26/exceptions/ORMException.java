package com.woniuxy.orm26.exceptions;

public class ORMException extends RuntimeException{

	public ORMException() {
		super();
	}

	public ORMException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ORMException(String message, Throwable cause) {
		super(message, cause);
	}

	public ORMException(String message) {
		super(message);
	}

	public ORMException(Throwable cause) {
		super(cause);
	}

	

}
