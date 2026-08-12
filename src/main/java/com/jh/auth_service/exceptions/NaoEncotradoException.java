package com.jh.auth_service.exceptions;

public class NaoEncotradoException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NaoEncotradoException(String objeto) {
		super(objeto + "não encontrado(a)");
	}
}
