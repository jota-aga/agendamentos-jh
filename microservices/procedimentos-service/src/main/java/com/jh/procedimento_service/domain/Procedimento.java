package com.jh.procedimento_service.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Procedimento {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String titulo;
	
	private String descricao;
	
	private BigDecimal preco;
	
	private Integer duracaoEmMinutos;
	
	private Boolean ativo;
	
	private LocalDateTime criadoEm;
	
	private LocalDateTime atualizadoEm;
	
	@ManyToOne
	private Categoria categoria;
}
