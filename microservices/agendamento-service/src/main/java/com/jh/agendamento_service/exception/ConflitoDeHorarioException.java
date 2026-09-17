package com.jh.agendamento_service.exception;

public class ConflitoDeHorarioException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ConflitoDeHorarioException() {
		super("Os horários informados geram conflito.");
	}
}
