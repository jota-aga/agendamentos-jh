package com.jh.agendamento_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.jh.agendamento_service.enums.AgendamentoStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AgendamentoAdminRequest(
		@Schema(description = "Id do usuário que marcou o agendamento. É opcional, pois caso um cliente chegue presencialmente, ele pode não ter cadastro no sistema",
		example = "1")
		Long usuarioId,

		@NotBlank(message = "Nome do usuário deve ser informado")
		@Schema(description = "Nome do usuário que marcou o agendamento", example = "João")
		String nomeDoUsuario,

		@NotNull(message = "Data não deve ser vazia") 
		@Schema(description = "Data a ser agendada", example = "2026-09-10")
		LocalDate data,

		@NotNull(message = "Inicio não deve ser vazio") 
		@Schema(description = "Horário a ser agendado", example = "14:00:00")
		LocalTime inicio,
		
		@NotNull(message = "Status não deve ser vazio") 
		@Schema(description = "Status do agendamento, aqui pode ser informado, pois é possível que chegue um cliente presencialemnte "
				+ "na barbearia, então, o profissional pode cadastrar o agendamento no sistema manualmente", 
				example = "CONCLUIDO")
		AgendamentoStatus status,

		@NotNull(message = "Id do procedimento não deve ser vazio") 
		@Schema(description = "Id do procedimento a ser procurado no microsservice procedimento", example = "1")
		Long procedimentoId
		) {

}
