package com.jh.procedimento_service.exceptions;

public class CategoriaRepetidaException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CategoriaRepetidaException() {
		super("Categoria com esse já existe");
	}
}
