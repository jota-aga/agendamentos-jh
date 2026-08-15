package com.jh.auth_service.exceptions;

public class NaoEncontradoException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NaoEncontradoException(String objeto) {
		super(objeto + "não encontrado(a)");
	}
}
