package com.jh.auth_service.exceptions;

public class LoginIncorretoException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LoginIncorretoException() {
		super("Email ou senha incorretos");
	}
}
