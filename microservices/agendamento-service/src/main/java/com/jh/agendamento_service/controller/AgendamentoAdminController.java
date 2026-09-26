package com.jh.agendamento_service.controller;

import java.time.LocalDate;
import java.time.LocalTime;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jh.agendamento_service.dto.AgendamentoAdminRequest;
import com.jh.agendamento_service.dto.AgendamentoResponse;
import com.jh.agendamento_service.dto.AgendamentoStatusRequest;
import com.jh.agendamento_service.enums.AgendamentoStatus;
import com.jh.agendamento_service.service.AgendamentoAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/agendamento/admin")
@RequiredArgsConstructor
public class AgendamentoAdminController {

	private final AgendamentoAdminService agendamentoAdminService;

	@PostMapping
	public ResponseEntity<?> criarAgendamento(@Valid @RequestBody AgendamentoAdminRequest agendamentoRequest) {
		agendamentoAdminService.criarAgendamento(agendamentoRequest);

		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<AgendamentoResponse> getAgendamentoPorId(@PathVariable String id){
		AgendamentoResponse response = agendamentoAdminService.getAgendamentoPorId(id);
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> atualizarAgendamento(@PathVariable String id,
			@Valid @RequestBody AgendamentoAdminRequest agendamentoRequest) {
		agendamentoAdminService.atualizarAgendamento(id, agendamentoRequest);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<?> atualizarStatusDoAgendamento(@PathVariable String id,
			@Valid @RequestBody AgendamentoStatusRequest statusRequest) {
		agendamentoAdminService.alterarStatusDoAgendamento(id, statusRequest);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping()
	public ResponseEntity<?> listarAgendamentoFiltrados(@RequestParam(required = false) Long usuarioId,
			@RequestParam(required = false) LocalDate data, @RequestParam(required = false) LocalTime inicioDoExpediente,
			@RequestParam(required = false) LocalTime fimDoExpediente, @RequestParam(required = false) String tituloDoProcedimento,
			@RequestParam(required = false) AgendamentoStatus status, @RequestParam(required = false) String sortBy) {
		List<AgendamentoResponse> response = agendamentoAdminService.listarAgendamentoFiltrados(usuarioId, data, inicioDoExpediente,
				fimDoExpediente, tituloDoProcedimento, status, sortBy);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
