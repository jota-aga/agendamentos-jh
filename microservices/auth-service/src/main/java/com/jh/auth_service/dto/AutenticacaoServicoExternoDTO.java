package com.jh.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AutenticacaoServicoExternoDTO(
		@NotNull(message = "id deve ser informado") 
		@Schema(description = "Id do serviço externo", example = "notificacao-service-id")
		String servicoId,
		@NotNull(message = "id deve ser informado") 
		@Schema(description = "Secret de acesso do serviço externo", example = "notificacao-service-secret")
		String servicoSecret) {

}
