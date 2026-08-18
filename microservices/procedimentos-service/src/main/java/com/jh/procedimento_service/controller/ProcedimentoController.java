package com.jh.procedimento_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jh.procedimento_service.domain.Procedimento;
import com.jh.procedimento_service.dto.ProcedimentoRequest;
import com.jh.procedimento_service.service.ProcedimentoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/procedimento")
@RequiredArgsConstructor
public class ProcedimentoController {
	
	private final ProcedimentoService procedimentoService;
	@PostMapping
	public ResponseEntity<?> criarProcedimento(@Valid @RequestBody ProcedimentoRequest procedimentoRequest){
		procedimentoService.criarProcedimento(procedimentoRequest);
		
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> atualizarProcedimento(@PathVariable Long id, @Valid @RequestBody ProcedimentoRequest procedimentoRequest){
		procedimentoService.atualizarProcedimento(id, procedimentoRequest);
		
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PatchMapping("/ativo/{id}")
	public ResponseEntity<?> alterarAtivo(@PathVariable Long id, @RequestBody Boolean ativo){
		procedimentoService.alterarAtivo(id, ativo);
		
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@GetMapping()
	public ResponseEntity<?> listarTodos(){
		List<Procedimento> procedimentos = procedimentoService.procurarTodos();
		
		return ResponseEntity.status(HttpStatus.OK).body(procedimentos);
	}
	
	@GetMapping("/ativos")
	public ResponseEntity<?> listarAtivos(){
		List<Procedimento> procedimentos = procedimentoService.procurarAtivos();
		
		return ResponseEntity.status(HttpStatus.OK).body(procedimentos);
	}
}
