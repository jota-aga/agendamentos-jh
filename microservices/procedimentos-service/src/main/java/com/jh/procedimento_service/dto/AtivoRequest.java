package com.jh.procedimento_service.dto;

import jakarta.validation.constraints.NotNull;

public record AtivoRequest(
		@NotNull(message = "O ativo é obrigatório")
		Boolean ativo) {

}
