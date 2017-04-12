package com.sunflower;

import java.io.IOException;

public class ExampleExceptionRethrowInvalid {
	public static void demoRethrow() throws IOException {
		try {
			throw new IOException("Error");
		} catch(IOException exception) {
			throw exception;
		}
	}
	
	public static void main(String[] args) {
		try {
			demoRethrow();
		} catch(IOException exception) {
			System.err.println(exception.getCause().getMessage());
		}
	}

}
