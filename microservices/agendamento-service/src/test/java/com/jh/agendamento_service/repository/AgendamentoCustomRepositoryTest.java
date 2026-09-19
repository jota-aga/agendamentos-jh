package com.jh.agendamento_service.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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

	private Agendamento agendamento;

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
		Agendamento agendamentoSalvo = new Agendamento("id", 1L, "usuario", LocalDateTime.now(), data, inicio, fim,
				"procedimento", 30, BigDecimal.ONE, "categoria", AgendamentoStatus.AGENDADO);

		agendamentoRepository.save(agendamentoSalvo);

		boolean existeConflito = agendamentoCustomRepository.existeConflitoDeHorario(data, fim,
				fim.plusMinutes(duracaoEmMinutosDoProcedimento), AgendamentoStatus.CANCELADO, null);

		assertFalse(existeConflito);
	}

	@Test
	public void naoDeveExistirConflitoDeHorarioQuandoInicioOuFimEstiverNoIntervaloDeUmAgendamentoCancelado() {
		Agendamento agendamentoSalvo = new Agendamento("id", 1L, "usuario", LocalDateTime.now(), data, inicio, fim,
				"procedimento", 30, BigDecimal.ONE, "categoria", AgendamentoStatus.CANCELADO);

		agendamentoRepository.save(agendamentoSalvo);

		boolean existeConflito = agendamentoCustomRepository.existeConflitoDeHorario(data, inicio, fim,
				AgendamentoStatus.CANCELADO, null);

		assertFalse(existeConflito);
	}

	@Test
	public void naoDeveExistirConflitoQuandoAgendamentoForOMesmo() {
		agendamento = criarAgendamento();

		boolean existeConflito = agendamentoCustomRepository.existeConflitoDeHorario(data, inicio, fim,
				AgendamentoStatus.CANCELADO, agendamento.getId());

		assertFalse(existeConflito);
	}

	@Test
	public void deveExistirConflitoDeHorarioQuandoInicioOuFimEstiverNoIntervaloDeUmAgendamento() {
		Agendamento agendamentoSalvo = new Agendamento("id", 1L, "usuario", LocalDateTime.now(), data, inicio, fim,
				"procedimento", duracaoEmMinutosDoProcedimento, BigDecimal.ONE, "categoria",
				AgendamentoStatus.AGENDADO);

		agendamentoRepository.save(agendamentoSalvo);

		boolean existeConflito = agendamentoCustomRepository.existeConflitoDeHorario(data, inicio, fim,
				AgendamentoStatus.CANCELADO, null);

		assertTrue(existeConflito);
	}

	@Test
	public void deveRetornarApenasUmAgendamentoQuandoFiltroForApenasPorUsuarioId() {
		agendamento = criarAgendamento();

		Agendamento agendamentoDiferente = new Agendamento("id diferente", 2L, "usuario diferente", LocalDateTime.now(),
				data.plusDays(1), inicio.minusHours(2), fim.minusHours(2), "procedimento diferente",
				duracaoEmMinutosDoProcedimento + 5, BigDecimal.TEN, "categoria", AgendamentoStatus.CONCLUIDO);
		agendamentoRepository.save(agendamentoDiferente);

		List<Agendamento> agendamentos = agendamentoCustomRepository
				.procurarAgendamentoPorFiltros(agendamento.getUsuarioId(), null, null, null, null, null, null);

		assertEquals(1, agendamentos.size());
		assertEquals(agendamento.getId(), agendamentos.get(0).getId());
	}

	@Test
	public void deveRetornarApenasUmAgendamentoQuandoFiltroForApenasPorData() {
		agendamento = criarAgendamento();

		Agendamento agendamentoDiferente = new Agendamento("id diferente", 2L, "usuario diferente", LocalDateTime.now(),
				data.plusDays(1), inicio.minusHours(2), fim.minusHours(2), "procedimento diferente",
				duracaoEmMinutosDoProcedimento + 5, BigDecimal.TEN, "categoria", AgendamentoStatus.CONCLUIDO);
		agendamentoRepository.save(agendamentoDiferente);

		List<Agendamento> agendamentos = agendamentoCustomRepository.procurarAgendamentoPorFiltros(null,
				agendamento.getData(), null, null, null, null, null);

		assertEquals(1, agendamentos.size());
		assertEquals(agendamento.getId(), agendamentos.get(0).getId());
	}

	@Test
	public void deveRetornarApenasUmAgendamentoQuandoFiltroForApenasPorInicio() {
		agendamento = criarAgendamento();

		Agendamento agendamentoDiferente = new Agendamento("id diferente", 2L, "usuario diferente", LocalDateTime.now(),
				data.plusDays(1), inicio.minusHours(2), fim.minusHours(2), "procedimento diferente",
				duracaoEmMinutosDoProcedimento + 5, BigDecimal.TEN, "categoria", AgendamentoStatus.CONCLUIDO);
		agendamentoRepository.save(agendamentoDiferente);

		List<Agendamento> agendamentos = agendamentoCustomRepository.procurarAgendamentoPorFiltros(null, null,
				agendamento.getInicio(), null, null, null, null);

		assertEquals(1, agendamentos.size());
		assertEquals(agendamento.getId(), agendamentos.get(0).getId());
	}

	@Test
	public void deveRetornarApenasUmAgendamentoQuandoFiltroForApenasPorFim() {
		agendamento = criarAgendamento();

		Agendamento agendamentoDiferente = new Agendamento("id diferente", 2L, "usuario diferente", LocalDateTime.now(),
				data.plusDays(1), inicio.minusHours(2), fim.minusHours(2), "procedimento diferente",
				duracaoEmMinutosDoProcedimento + 5, BigDecimal.TEN, "categoria", AgendamentoStatus.CONCLUIDO);
		agendamentoRepository.save(agendamentoDiferente);

		List<Agendamento> agendamentos = agendamentoCustomRepository.procurarAgendamentoPorFiltros(null, null, null,
				agendamentoDiferente.getFim(), null, null, null);

		assertEquals(1, agendamentos.size());
		assertEquals(agendamentoDiferente.getId(), agendamentos.get(0).getId());
	}

	@Test
	public void deveRetornarApenasUmAgendamentoQuandoFiltroForApenasPorTituloDoProcedimento() {
		agendamento = criarAgendamento();

		Agendamento agendamentoDiferente = new Agendamento("id diferente", 2L, "usuario diferente", LocalDateTime.now(),
				data.plusDays(1), inicio.minusHours(2), fim.minusHours(2), "procedimento diferente",
				duracaoEmMinutosDoProcedimento + 5, BigDecimal.TEN, "categoria", AgendamentoStatus.CONCLUIDO);
		agendamentoRepository.save(agendamentoDiferente);

		List<Agendamento> agendamentos = agendamentoCustomRepository.procurarAgendamentoPorFiltros(null, null, null,
				null, agendamento.getTituloDoProcedimento(), null, null);

		assertEquals(1, agendamentos.size());
		assertEquals(agendamento.getId(), agendamentos.get(0).getId());
	}

	@Test
	public void deveRetornarApenasUmAgendamentoQuandoFiltroForApenasPorStatus() {
		agendamento = criarAgendamento();

		Agendamento agendamentoDiferente = new Agendamento("id diferente", 2L, "usuario diferente", LocalDateTime.now(),
				data.plusDays(1), inicio.minusHours(2), fim.minusHours(2), "procedimento diferente",
				duracaoEmMinutosDoProcedimento + 5, BigDecimal.TEN, "categoria", AgendamentoStatus.CONCLUIDO);
		agendamentoDiferente = agendamentoRepository.save(agendamentoDiferente);

		List<Agendamento> agendamentos = agendamentoCustomRepository.procurarAgendamentoPorFiltros(null, null, null,
				null, null, agendamento.getStatus(), null);

		assertEquals(1, agendamentos.size());
		assertEquals(agendamento.getId(), agendamentos.get(0).getId());
	}

	@Test
	public void deveRetornarAListaFiltradaOrdenadaPorData() {
		agendamento = criarAgendamento();

		Agendamento agendamentoDiferente = new Agendamento("id diferente", 2L, "usuario diferente", LocalDateTime.now(),
				data.plusDays(1), inicio.minusHours(2), fim.minusHours(2), "procedimento diferente",
				duracaoEmMinutosDoProcedimento + 5, BigDecimal.TEN, "categoria", AgendamentoStatus.CONCLUIDO);
		agendamentoDiferente = agendamentoRepository.save(agendamentoDiferente);

		List<Agendamento> agendamentos = agendamentoCustomRepository.procurarAgendamentoPorFiltros(null, null, null,
				null, null, null, "data");

		assertEquals(2, agendamentos.size());
		assertEquals(agendamentoDiferente.getId(), agendamentos.get(0).getId());
		assertEquals(agendamento.getId(), agendamentos.get(1).getId());
	}

	@Test
	public void deveRetornarAListaFiltradaOrdenadaPorInicio() {
		agendamento = criarAgendamento();

		Agendamento agendamentoDiferente = new Agendamento("id diferente", 2L, "usuario diferente", LocalDateTime.now(),
				data.plusDays(1), inicio.minusHours(2), fim.minusHours(2), "procedimento diferente",
				duracaoEmMinutosDoProcedimento + 5, BigDecimal.TEN, "categoria", AgendamentoStatus.CONCLUIDO);
		agendamentoDiferente = agendamentoRepository.save(agendamentoDiferente);

		List<Agendamento> agendamentos = agendamentoCustomRepository.procurarAgendamentoPorFiltros(null, null, null,
				null, null, null, "inicio");

		assertEquals(2, agendamentos.size());
		assertEquals(agendamento.getId(), agendamentos.get(0).getId());
		assertEquals(agendamentoDiferente.getId(), agendamentos.get(1).getId());
	}

	@Test
	public void deveRetornarAListaFiltradaOrdenadaPorCriadoEm() {
		agendamento = criarAgendamento();

		Agendamento agendamentoDiferente = new Agendamento("id diferente", 2L, "usuario diferente",
				LocalDateTime.now().plusSeconds(5), data.plusDays(1), inicio.minusHours(2), fim.minusHours(2),
				"procedimento diferente", duracaoEmMinutosDoProcedimento + 5, BigDecimal.TEN, "categoria",
				AgendamentoStatus.CONCLUIDO);
		agendamentoDiferente = agendamentoRepository.save(agendamentoDiferente);

		List<Agendamento> agendamentos = agendamentoCustomRepository.procurarAgendamentoPorFiltros(null, null, null,
				null, null, null, "criadoEm");

		assertEquals(2, agendamentos.size());
		assertEquals(agendamentoDiferente.getId(), agendamentos.get(0).getId());
		assertEquals(agendamento.getId(), agendamentos.get(1).getId());
	}

	@Test
	public void deveRetornarAListaFiltradaOrdenadaPorStatus() {
		agendamento = criarAgendamento();

		Agendamento agendamentoDiferente = new Agendamento("id diferente", 2L, "usuario diferente",
				LocalDateTime.now().plusSeconds(5), data.plusDays(1), inicio.minusHours(2), fim.minusHours(2),
				"procedimento diferente", duracaoEmMinutosDoProcedimento + 5, BigDecimal.TEN, "categoria",
				AgendamentoStatus.CONCLUIDO);
		agendamentoDiferente = agendamentoRepository.save(agendamentoDiferente);

		List<Agendamento> agendamentos = agendamentoCustomRepository.procurarAgendamentoPorFiltros(null, null, null,
				null, null, null, "status");

		assertEquals(2, agendamentos.size());
		assertEquals(agendamentoDiferente.getId(), agendamentos.get(0).getId());
		assertEquals(agendamento.getId(), agendamentos.get(1).getId());
	}

	@Test
	public void deveRetornarApenasUmAgendamentoPorProcuraFiltrada() {
		agendamento = criarAgendamento();

		Agendamento agendamentoDiferente = new Agendamento("id diferente", 2L, "usuario diferente",
				LocalDateTime.now().plusSeconds(5), data.plusDays(1), inicio.minusHours(2), fim.minusHours(2),
				"procedimento diferente", duracaoEmMinutosDoProcedimento + 5, BigDecimal.TEN, "categoria",
				AgendamentoStatus.CONCLUIDO);
		agendamentoDiferente = agendamentoRepository.save(agendamentoDiferente);

		List<Agendamento> agendamentos = agendamentoCustomRepository.procurarAgendamentoPorFiltros(
				agendamento.getUsuarioId(), agendamento.getData(), agendamento.getInicio(), agendamento.getFim(),
				agendamento.getTituloDoProcedimento(), agendamento.getStatus(), "status");

		assertEquals(1, agendamentos.size());
		assertEquals(agendamento.getId(), agendamentos.get(0).getId());
	}
	
	@Test
	public void deveRetornarTodosOsAgendamentosOrdenadosPorDataQuandoNenhumParametroForInformado() {
		agendamento = criarAgendamento();

		Agendamento agendamentoDiferente = new Agendamento("id diferente", 2L, "usuario diferente",
				LocalDateTime.now().plusSeconds(5), data.plusDays(1), inicio.minusHours(2), fim.minusHours(2),
				"procedimento diferente", duracaoEmMinutosDoProcedimento + 5, BigDecimal.TEN, "categoria",
				AgendamentoStatus.CONCLUIDO);
		agendamentoDiferente = agendamentoRepository.save(agendamentoDiferente);

		List<Agendamento> agendamentos = agendamentoCustomRepository.procurarAgendamentoPorFiltros(null, null, null,
				null, null, null, null);

		assertEquals(2, agendamentos.size());
		assertEquals(agendamentoDiferente.getId(), agendamentos.get(0).getId());
		assertEquals(agendamento.getId(), agendamentos.get(1).getId());
	}

	private Agendamento criarAgendamento() {
		Agendamento agendamentoSalvo = new Agendamento("id", 1L, "usuario", LocalDateTime.now(), data, inicio, fim,
				"procedimento", duracaoEmMinutosDoProcedimento, BigDecimal.ONE, "categoria",
				AgendamentoStatus.AGENDADO);

		return agendamentoRepository.save(agendamentoSalvo);
	}
}
