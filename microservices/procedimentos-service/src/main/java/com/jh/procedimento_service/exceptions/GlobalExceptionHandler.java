package com.jh.procedimento_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(CategoriaRepetidaException.class)
	public ResponseEntity<String> handlerCategoriaRepetidaException(CategoriaRepetidaException ex){
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}
}
