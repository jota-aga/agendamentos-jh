package com.jh.procedimento_service.dto.procedimento;

import java.math.BigDecimal;

import com.jh.procedimento_service.dto.procedimento.categoria.CategoriaResponse;

public record ProcedimentoResponse(Long id, String titulo, String descricao, BigDecimal preco, Integer duracaoEmMinutos, 
		Boolean ativo, CategoriaResponse categoria) {

}
