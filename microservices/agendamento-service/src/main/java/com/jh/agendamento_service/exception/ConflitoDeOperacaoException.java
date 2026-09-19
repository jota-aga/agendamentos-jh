package com.jh.agendamento_service.exception;

public class ConflitoDeOperacaoException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ConflitoDeOperacaoException(String message) {
		super(message);
	}
}
