package com.jh.agendamento_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

public record AgendamentoRequest(
		@NotNull(message = "Data não deve ser vazia")
		LocalDate data,
		
		@NotNull(message = "Inicio não deve ser vazio")
		LocalTime inicio,
		
		@NotNull(message = "Id do procedimento não deve ser vazio")
		Long procedimentoId
		) 

		
{}
