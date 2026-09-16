package com.jh.agendamento_service.dto;

import java.math.BigDecimal;

public record ProcedimentoResponse(Long id, String titulo, String descricao, BigDecimal preco, Integer duracaoEmMinutos, 
		Boolean ativo, CategoriaResponse categoria) {

}
