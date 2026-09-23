package com.jh.agendamento_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jh.agendamento_service.dto.AgendamentoRequest;
import com.jh.agendamento_service.dto.AgendamentoResponse;
import com.jh.agendamento_service.service.AgendamentoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/agendamento")
@RequiredArgsConstructor
public class AgendamentoController {
	
	private final AgendamentoService agendamentoService;
	
	@PostMapping
	public ResponseEntity<?> criarAgendamento(@Valid @RequestBody AgendamentoRequest agendamentoRequest){
		agendamentoService.criarAgendamento(agendamentoRequest);
		
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> atualizarAgendamentoComoCliente(@PathVariable String id, @Valid @RequestBody AgendamentoRequest agendamentoRequest){
		agendamentoService.atualizarAgendamento(id, agendamentoRequest);
		
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> procurarAgendamentoPorId(@PathVariable String id){
		AgendamentoResponse response = agendamentoService.procurarAgendamentoPorId(id);
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping()
	public ResponseEntity<?> listarAgendamentoDoUsuario(){
		List<AgendamentoResponse> response = agendamentoService.listarAgendamentosDoUsuario();
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
}
