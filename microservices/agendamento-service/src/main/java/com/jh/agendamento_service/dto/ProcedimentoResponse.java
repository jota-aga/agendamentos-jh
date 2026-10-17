package com.jh.agendamento_service.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProcedimentoResponse(
		@Schema(example = "1")
		Long id,
		@Schema(example = "Corte de Cabelo")
		String titulo,
		@Schema(example = "Corte de cabelo incluído máquina e teosura")
		String descricao,
		@Schema(example = "20.00")
		BigDecimal preco,
		@Schema(example = "30")
		Integer duracaoEmMinutos,
		@Schema(example = "true")
		Boolean ativo,
		CategoriaResponse categoria) {

}
