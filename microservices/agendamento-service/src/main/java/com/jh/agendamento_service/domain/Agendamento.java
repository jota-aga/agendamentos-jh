package com.jh.agendamento_service.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.mongodb.core.mapping.Document;

import com.jh.agendamento_service.enums.AgendamentoStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	private String id;
	
	private Long usuarioId;
	
	private String nomeDoUsuario;
	
	private LocalDateTime criadoEm;
	
	private LocalDate data;
	
	private LocalTime inicio;
	
	private LocalTime fim;
	
	private String tituloDoProcedimento;
	
	private Integer duracaoEmMinutos;
	
	private BigDecimal preco;
	
	private String nomeDaCategoria;
	
	@Enumerated(value = EnumType.STRING)
	private AgendamentoStatus status;
}
