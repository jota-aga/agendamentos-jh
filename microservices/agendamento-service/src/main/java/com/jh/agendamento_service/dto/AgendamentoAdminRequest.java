package com.jh.agendamento_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.jh.agendamento_service.enums.AgendamentoStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AgendamentoAdminRequest(
		@NotNull(message = "Id do usuário deve ser informado") Long usuarioId,

		@NotBlank(message = "Nome do usuário deve ser informado") String nomeDoUsuario,

		@NotNull(message = "Data não deve ser vazia") LocalDate data,

		@NotNull(message = "Inicio não deve ser vazio") LocalTime inicio,
		
		@NotNull(message = "Status não deve ser vazio") AgendamentoStatus status,

		@NotNull(message = "Id do procedimento não deve ser vazio") Long procedimentoId
		) {

}
