package com.jh.auth_service.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.jh.auth_service.dto.ErrorResponse;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex){
		Map<String, String> errors = new HashMap<>();
		ex.getConstraintViolations()
			.forEach(constraint -> errors.put(constraint.getPropertyPath().toString(), constraint.getMessage()));
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}
	
	@ExceptionHandler(LoginIncorretoException.class)
	public ResponseEntity<?> handleLoginIncorretoException(LoginIncorretoException ex){
		ErrorResponse response = new ErrorResponse(ex.getClass().toString(), ex.getMessage());
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}
	
	@ExceptionHandler(NaoEncontradoException.class)
	public ResponseEntity<?> handleNaoEncotradoException(NaoEncontradoException ex){
		ErrorResponse response = new ErrorResponse(ex.getClass().toString(), ex.getMessage());
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}
	
	@ExceptionHandler(EmailRepetidoExecption.class)
	public ResponseEntity<?> handleEmailRepetidoExecption(EmailRepetidoExecption ex){
		ErrorResponse response = new ErrorResponse(ex.getClass().toString(), ex.getMessage());
		
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}
}
