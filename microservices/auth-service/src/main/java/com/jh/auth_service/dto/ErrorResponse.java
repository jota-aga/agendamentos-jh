package com.jh.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(
		@Schema(example = "erro")
		String error, 
		@Schema(example = "mensagem de erro")
		String mensagem
		) {

}
