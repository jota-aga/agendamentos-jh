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

import com.jh.agendamento_service.dto.AgendamentoRequest;
import com.jh.agendamento_service.dto.AgendamentoResponse;
import com.jh.agendamento_service.service.AgendamentoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/agendamento")
@RequiredArgsConstructor
@Tag(
		name = "Agendamento Client", 
		description = "Gerenciamento dos agendamentos do usuário autenticado. Os usuários são limitado a gerenciar apenas os seus agendamentos.")
public class AgendamentoController {

	private final AgendamentoService agendamentoService;

	@PostMapping
	@Operation(summary = "Criar agendamento", description = "Cria um agendamento para o usuário")
	public ResponseEntity<?> criarAgendamento(@Valid @RequestBody AgendamentoRequest agendamentoRequest) {
		agendamentoService.criarAgendamento(agendamentoRequest);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualizar agendamento", description = "Atualiza um agendamento para o usuário")
	public ResponseEntity<?> atualizarAgendamento(@PathVariable String id,
			@Valid @RequestBody AgendamentoRequest agendamentoRequest) {
		agendamentoService.atualizarAgendamento(id, agendamentoRequest);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PatchMapping("/{id}/cancelar")
	@Operation(summary = "Cancelar agendamento", description = "Cancela um agendamento do usuário que estava como agendado. Se já foi concluído, não será possível cancelar")
	public ResponseEntity<?> cancelarAgendamento(@PathVariable String id) {
		agendamentoService.cancelarAgendamento(id);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Procurar agendamento por id", description = "Procura um agendamento por id. Caso o agendamento não seja do usuário, ele não terá acesso")
	public ResponseEntity<?> procurarAgendamentoPorId(@PathVariable String id) {
		AgendamentoResponse response = agendamentoService.procurarAgendamentoPorId(id);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping
	@Operation(summary = "Listar agendamentos", description = "Lista agendamentos do usuário")
	public ResponseEntity<?> listarAgendamentos() {
		List<AgendamentoResponse> response = agendamentoService.listarAgendamentos();

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/horarios")	@Operation(summary = "Listar horários livres", description = "Lista hoários livres de acordo com o tempo que o procedimento requerido leva dentro de um intevalo de inicio e fim de expediente")
	public ResponseEntity<?> listarHorariosDisponiveis(@RequestParam LocalTime inicioDoExpediente,
			@RequestParam LocalTime fimDoExepediente, @RequestParam LocalDate data, @RequestParam Long procedimentoId) {
		List<LocalTime> horariosDisponiveis = agendamentoService.horariosDisponiveis(inicioDoExpediente, fimDoExepediente, data, procedimentoId);

		return ResponseEntity.status(HttpStatus.OK).body(horariosDisponiveis);
	}
}
