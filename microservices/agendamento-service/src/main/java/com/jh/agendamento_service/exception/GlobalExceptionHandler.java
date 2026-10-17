package com.jh.agendamento_service.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
		
		ProblemDetail problemDetail =  ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errors.toString());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex){
		Map<String, String> errors = new HashMap<>();
		ex.getConstraintViolations()
			.forEach(constraint -> errors.put(constraint.getPropertyPath().toString(), constraint.getMessage()));
		
		ProblemDetail problemDetail =  ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errors.toString());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
	}
	
	@ExceptionHandler(ConflitoDeHorarioException.class)
	public ResponseEntity<?> handleConflitoDeHorarioException(ConflitoDeHorarioException ex){
		ProblemDetail problemDetail =  ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
		
		return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
	}
	
	@ExceptionHandler(ConflitoDeOperacaoException.class)
	public ResponseEntity<?> handleConflitoDeOperacaoException(ConflitoDeOperacaoException ex){
		ProblemDetail problemDetail =  ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
		
		return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
	}
	
	@ExceptionHandler(NaoEncontradoException.class)
	public ResponseEntity<?> handleNaoEncontradoException(NaoEncontradoException ex){
		ProblemDetail problemDetail =  ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
	}
	
	@ExceptionHandler(ProcedimentoNaoDisponivelException.class)
	public ResponseEntity<?> handleProcedimentoNaoDisponivelException(ProcedimentoNaoDisponivelException ex){
		ProblemDetail problemDetail =  ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
		
		return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
	}
	
	@ExceptionHandler(NaoAutorizadoException.class)
	public ResponseEntity<?> handleNaoAutorizadoException(NaoAutorizadoException ex){
		ProblemDetail problemDetail =  ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
	}
}
