package com.jh.agendamento_service.exception;

public class NaoEncontradoException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NaoEncontradoException(String objeto) {
		super(objeto+" não foi encontrado.");
	}
}
