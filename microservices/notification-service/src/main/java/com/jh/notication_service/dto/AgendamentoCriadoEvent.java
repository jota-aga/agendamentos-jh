package com.jh.notication_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoCriadoEvent(Long usuarioId, String tituloDoProcedimento, LocalDate data, LocalTime inicio) {
}
