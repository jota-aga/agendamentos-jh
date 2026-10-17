package com.jh.agendamento_service.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/agendamento/admin")
@RequiredArgsConstructor
@Tag(name = "Agendamento Admin", description = "Gerenciamento dos agendamento como administrador.")
public class AgendamentoAdminController {

	private final AgendamentoAdminService agendamentoAdminService;

	@PostMapping
	@Operation(summary = "Criar Agendamento", 
			description = "Cria um agendamento para algum cliente. O usuarioId é opcional, pois o cliente pode não ter cadastro no sistema")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Agendamento criado com sucesso."),
			@ApiResponse(responseCode = "409", description = "Conflito de horário.", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)) }),
			@ApiResponse(responseCode = "404", description = "Procedimento não encontrado.", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)) })
			
	})
	public ResponseEntity<?> criarAgendamento(@Valid @RequestBody AgendamentoAdminRequest agendamentoRequest) {
		agendamentoAdminService.criarAgendamento(agendamentoRequest);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@GetMapping("/{id}")
	@Operation(summary = "Procurar Agendamento por id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Agendamento encontrado com sucesso.", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = AgendamentoResponse.class)) }),
			@ApiResponse(responseCode = "404", description = "Agendamento não encontrado.", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)) })
			
	})
	public ResponseEntity<AgendamentoResponse> getAgendamentoPorId(@PathVariable String id){
		AgendamentoResponse response = agendamentoAdminService.getAgendamentoPorId(id);
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualiza um Agendamento")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Agendamento atualizado com sucesso."),
			@ApiResponse(responseCode = "409", description = "Conflito de horário.", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)) }),
			@ApiResponse(responseCode = "404",  description = "Procedimento ou Agendamento não encontrado.", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)) })
	})
	public ResponseEntity<?> atualizarAgendamento(@PathVariable String id,
			@Valid @RequestBody AgendamentoAdminRequest agendamentoRequest) {
		agendamentoAdminService.atualizarAgendamento(id, agendamentoRequest);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PatchMapping("/{id}/status")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Status do agendamento atualizado com sucesso."),
			@ApiResponse(responseCode = "404",  description = "Agendamento não encontrado.", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)) })
	})
	@Operation(summary = "Atualizar status do agendamento")
	public ResponseEntity<?> atualizarStatusDoAgendamento(@PathVariable String id,
			@Valid @RequestBody AgendamentoStatusRequest statusRequest) {
		agendamentoAdminService.alterarStatusDoAgendamento(id, statusRequest);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping
	@Operation(summary = "Listar agendamentos filtrados", 
	description = "Caso queira listar todos os agendamentos, basta não passar nenhum parâmetro na requisição.")
	@ApiResponse(responseCode = "200", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = AgendamentoResponse[].class)) })
	public ResponseEntity<?> listarAgendamentoFiltrados(@RequestParam(required = false) Long usuarioId,
			@RequestParam(required = false) LocalDate data, @RequestParam(required = false) LocalTime inicioDoExpediente,
			@RequestParam(required = false) LocalTime fimDoExpediente, @RequestParam(required = false) String tituloDoProcedimento,
			@RequestParam(required = false) AgendamentoStatus status, @RequestParam(required = false) String sortBy) {
		List<AgendamentoResponse> response = agendamentoAdminService.listarAgendamentoFiltrados(usuarioId, data, inicioDoExpediente,
				fimDoExpediente, tituloDoProcedimento, status, sortBy);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
