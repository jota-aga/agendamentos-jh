package com.jh.procedimento_service.dto.procedimento;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProcedimentoRequest(
		@NotBlank(message = "Título é obrigatório")
		@Schema(description = "Título do procedimento", example = "Corte de cabelo")
		String titulo,
		
		@NotBlank(message = "Descrição é obrigatório")
		@Schema(description = "Descrição do procedimento", example = "Corte inclui máquina e tesoura")
		String descricao,
		
		@NotNull(message = "Preço é obrigatório")
		@Positive(message = "Preço deve ser positivo")
		@Schema(description = "Preço do procedimento", example = "30")
		BigDecimal preco,
		
		@NotNull(message = "Duração em minutos é obrigatório")
		@Positive(message = "Duração em minutos deve ser positivo")
		@Schema(description = "Duração em minutos procedimento", example = "30")
		Integer duracaoEmMinutos,
		
		@NotNull(message = "CategoriaId é obrigatório")
		@Schema(description = "Id de uma categoria já cadastrada", example = "1L")
		Long categoriaId
		
		)
{

}
