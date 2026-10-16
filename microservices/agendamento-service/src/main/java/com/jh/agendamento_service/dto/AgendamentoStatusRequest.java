package com.jh.agendamento_service.dto;

import com.jh.agendamento_service.enums.AgendamentoStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AgendamentoStatusRequest(
		@NotNull(message="status deve ser informado") 
		@Schema(description = "Novo valor do status", example = "CANCELADO")
		AgendamentoStatus status
		) {

}
