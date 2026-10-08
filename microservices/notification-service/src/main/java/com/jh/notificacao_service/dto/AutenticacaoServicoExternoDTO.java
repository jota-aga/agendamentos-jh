package com.jh.notificacao_service.dto;

import jakarta.validation.constraints.NotNull;

public record AutenticacaoServicoExternoDTO(
		@NotNull(message = "id deve ser informado") String servicoId,
		@NotNull(message = "id deve ser informado") String servicoSecret) {

}
