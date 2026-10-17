package com.jh.agendamento_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.jh.agendamento_service.enums.AgendamentoStatus;

import io.swagger.v3.oas.annotations.media.Schema;

public record AgendamentoResponse(@Schema(example = "dksjfafnja") String id,

		@Schema(example = "1") 		Long usuarioId,
		
		@Schema(example = "João Henrique") 		String nomeDoUsuario,

		@Schema(example = "14:00:00") 		LocalDateTime criadoEm,

		@Schema(example = "2026-10-09") 		LocalDate data,

		@Schema(example = "14:00:00") 		LocalTime inicio,

		@Schema(example = "14:30:00") 		LocalTime fim,

		@Schema(example = "Corte de cabelo") 		String tituloDoProcedimento,

		@Schema(example = "30") 
		Integer duracaoEmMinutos,

		@Schema(example = "25.00") 
		BigDecimal preco,

		@Schema(example = "Cabelo") 
		String nomeDaCategoria,
		
		AgendamentoStatus status) {

}
