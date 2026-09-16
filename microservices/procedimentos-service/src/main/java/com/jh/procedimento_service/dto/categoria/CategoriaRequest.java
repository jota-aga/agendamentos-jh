package com.jh.procedimento_service.dto.categoria;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(
		@NotBlank(message = "Nome é obrigatório")
		String nome
		) {}
