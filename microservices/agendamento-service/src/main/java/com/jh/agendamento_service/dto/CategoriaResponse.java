package com.jh.agendamento_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CategoriaResponse(
		@Schema(example = "1")
		Long id, 
		@Schema(example = "Cabelo")
		String nome, 
		@Schema(example = "true")
		Boolean ativo) {

}
