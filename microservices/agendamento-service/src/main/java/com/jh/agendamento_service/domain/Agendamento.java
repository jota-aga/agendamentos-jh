package com.jh.agendamento_service.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Agendamento {
	
	@Id
	private Long id;
	
	private LocalDateTime criadoEm;
	
	private LocalDate data;
	
	private LocalDateTime inicio;
	
	private LocalDateTime fim;
	
	private String nomeDoProcedimento;
	
	private Integer duracaoEmMinutos;
	
	private BigDecimal valor;
	
	private String nomeDaCategoria;
	
	
}
