package com.zipservlet.thread;

public class ThreadProcessException extends Exception {
	private static final long serialVersionUID = 1L;

	public ThreadProcessException(Exception e) {
		super(e);
	}
}
