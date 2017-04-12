package com.sunflower.repository;

public class StorageException extends RuntimeException {

	private static final long serialVersionUID = 810958870634192243L;

	public StorageException(String message) {
		super(message);
	}
	
	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}

}
