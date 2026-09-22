package com.jh.agendamento_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.jh.agendamento_service.enums.AgendamentoStatus;

public record AgendamentoResponse(
		 String id,
		
		 Long usuarioId,
		
		 String nomeDoUsuario,
		
		 LocalDateTime criadoEm,
		
		 LocalDate data,
		
		 LocalTime inicio,
		
		 LocalTime fim,
		
		 String tituloDoProcedimento,
		
		 Integer duracaoEmMinutos,
		
		 BigDecimal preco,
		
		 String nomeDaCategoria,
		
		 AgendamentoStatus status
		) {

}
