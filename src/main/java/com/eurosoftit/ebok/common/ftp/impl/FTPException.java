package com.eurosoftit.ebok.common.ftp.impl;

public class FTPException extends Exception {
	public FTPException(String language) {
		super(language);
	}

	public FTPException(String language, Throwable cause) {
		super(language,cause);
	}
}
