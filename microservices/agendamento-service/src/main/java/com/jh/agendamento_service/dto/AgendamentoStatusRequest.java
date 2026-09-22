package com.jh.agendamento_service.dto;

import com.jh.agendamento_service.enums.AgendamentoStatus;

import jakarta.validation.constraints.NotNull;

public record AgendamentoStatusRequest(@NotNull(message="status deve ser informado") AgendamentoStatus status) {

}
