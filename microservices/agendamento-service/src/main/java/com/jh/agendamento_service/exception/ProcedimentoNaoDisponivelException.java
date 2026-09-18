package com.jh.agendamento_service.exception;

public class ProcedimentoNaoDisponivelException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ProcedimentoNaoDisponivelException() {
		super("Procedimento não está disponível");
	}
}
