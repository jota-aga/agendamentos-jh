package com.jh.agendamento_service.exception;

public class NaoAutorizadoException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NaoAutorizadoException(String message) {
		super(message);
	}
}
