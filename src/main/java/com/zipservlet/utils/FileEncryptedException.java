package com.zipservlet.utils;

public class FileEncryptedException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public FileEncryptedException(String fileName) {
		super("Unable to open encrypted file " + fileName);
	}
	
}
