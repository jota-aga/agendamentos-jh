package com.jh.agendamento_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public record AgendamentoRequest(
		@NotNull(message = "Data não deve ser vazia")
		@FutureOrPresent(message = "A data deve ser no present ou futuro")
		@Schema(description = "Data a ser agendada", example = "2026-09-10")
		LocalDate data,
		
		@NotNull(message = "Inicio não deve ser vazio")
		@Schema(description = "Horário a ser agendado", example = "14:00:00")
		LocalTime inicio,
		
		@NotNull(message = "Id do procedimento não deve ser vazio")
		@Schema(description = "Id do procedimento a ser procurado no microsservice procedimento", example = "1")
		Long procedimentoId
		) 

		
{}
