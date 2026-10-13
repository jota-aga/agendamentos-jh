package com.jh.procedimento_service.dto.categoria;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(
		@NotBlank(message = "Nome é obrigatório")
		@Schema(description = "Nome da categoria", example = "categoria")
		String nome
		) {}
