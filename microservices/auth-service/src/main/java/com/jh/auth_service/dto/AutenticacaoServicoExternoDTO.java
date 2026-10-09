package com.jh.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AutenticacaoServicoExternoDTO(
		@NotBlank(message = "id deve ser informado") 
		@Schema(description = "Id do serviço externo", example = "notificacao-service-id")
		String servicoId,
		@NotBlank(message = "id deve ser informado") 
		@Schema(description = "Secret de acesso do serviço externo", example = "notificacao-service-secret")
		String servicoSecret) {

}
