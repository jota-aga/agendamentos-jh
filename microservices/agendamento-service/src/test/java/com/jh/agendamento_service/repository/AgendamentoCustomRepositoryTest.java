package com.jh.agendamento_service.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;

import com.jh.agendamento_service.domain.Agendamento;
import com.jh.agendamento_service.enums.AgendamentoStatus;

@DataMongoTest
@Import(AgendamentoCustomRepository.class)
public class AgendamentoCustomRepositoryTest {
	
	@Autowired
	private AgendamentoRepository agendamentoRepository;
	
	@Autowired
	private AgendamentoCustomRepository agendamentoCustomRepository;
	
	private LocalDate data;
	
	private LocalTime inicio;
	
	private LocalTime fim;
	
	private Integer duracaoEmMinutosDoProcedimento;
	
	@BeforeEach
	public void setUp() {
		agendamentoRepository.deleteAll();
		duracaoEmMinutosDoProcedimento = 30;
		data = LocalDate.of(2026, 9, 19);
		inicio = LocalTime.of(2, 0);
		fim = inicio.plusMinutes(duracaoEmMinutosDoProcedimento);
	}
	
	@Test
	public void naoDeveExistirConflitoDeHorarioQuanoInicioEFimNaoEstiveremEmNenhumIntervaloDeOutroAgendamento() {
		Agendamento agendamentoSalvo =  new Agendamento("id", 1L, "usuario", LocalDateTime.now(), data, 
				inicio, fim, "procedimento", 30, BigDecimal.ONE, "categoria", AgendamentoStatus.AGENDADO);
		
		agendamentoRepository.save(agendamentoSalvo);
		
		boolean existeConflito = agendamentoCustomRepository.existeConflitoDeHorario(data, fim, 
				fim.plusMinutes(duracaoEmMinutosDoProcedimento), AgendamentoStatus.CANCELADO, null);
		
		assertFalse(existeConflito);
	}
	
	@Test
	public void naoDeveExistirConflitoDeHorarioQuandoInicioOuFimEstiverNoIntervaloDeUmAgendamentoCancelado() {
		Agendamento agendamentoSalvo =  new Agendamento("id", 1L, "usuario", LocalDateTime.now(), data, 
				inicio, fim, "procedimento", 30, BigDecimal.ONE, "categoria", AgendamentoStatus.CANCELADO);
		
		agendamentoRepository.save(agendamentoSalvo);
		
		boolean existeConflito = agendamentoCustomRepository.existeConflitoDeHorario(data, inicio, 
				fim, AgendamentoStatus.CANCELADO, null);
		
		assertFalse(existeConflito);
	}
	
	@Test
	public void deveExistirConflitoDeHorarioQuandoInicioOuFimEstiverNoIntervaloDeUmAgendamento() {
		Agendamento agendamentoSalvo =  new Agendamento("id", 1L, "usuario", LocalDateTime.now(), data, 
				inicio, fim, "procedimento", duracaoEmMinutosDoProcedimento, BigDecimal.ONE, "categoria", AgendamentoStatus.AGENDADO);
		
		agendamentoRepository.save(agendamentoSalvo);
		
		boolean existeConflito = agendamentoCustomRepository.existeConflitoDeHorario(data, inicio, 
				fim, AgendamentoStatus.CANCELADO, null);
		
		assertTrue(existeConflito);
	}
	
	
}
