package com.jh.procedimento_service.dto;

import java.math.BigDecimal;

public record ProcedimentoResponse(String titulo, String descricao, BigDecimal preco, Integer duracaoEmMinutos, String categoriaNome) {

}
