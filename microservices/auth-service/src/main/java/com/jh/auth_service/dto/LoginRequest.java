package com.jh.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
		@Schema(example = "joao@email.com")
		@NotBlank(message = "Email deve ser informado.") 
		String email,
		
		@Schema(example = "joao1234")
		@NotBlank(message = "Senha deve ser informado.") 
		String senha) {
}
