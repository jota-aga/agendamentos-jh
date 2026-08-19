package com.jh.procedimento_service.dto.procedimento;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProcedimentoRequest(
		@NotBlank(message = "Título é obrigatório")
		String titulo,
		
		@NotBlank(message = "Descrição é obrigatório")
		String descricao,
		
		@NotNull(message = "Preço é obrigatório")
		BigDecimal preco,
		
		@NotNull(message = "Duração em minutos é obrigatório")
		Integer duracaoEmMinutos,
		
		@NotNull(message = "CategoriaId é obrigatório")
		Long categoriaId
		
		)
{

}
