package com.jh.auth_service.exceptions;

public class EmailRepetidoExecption extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmailRepetidoExecption() {
		super("Email já cadastrado no sistema");
	}
}
