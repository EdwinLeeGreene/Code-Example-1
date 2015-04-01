package com.zipservlet.process;

public class FileWriteException extends Exception {
	private static final long serialVersionUID = 1L;

	public FileWriteException(Exception e) {
		super(e);
	}
}
