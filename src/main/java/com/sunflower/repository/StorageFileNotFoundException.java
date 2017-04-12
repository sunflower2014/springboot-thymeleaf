package com.sunflower.repository;

public class StorageFileNotFoundException extends StorageException {

	private static final long serialVersionUID = 2705561406820137349L;
	
	public StorageFileNotFoundException(String message) {
		super(message);
	}
	
	public StorageFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
