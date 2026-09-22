package com.jh.agendamento_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import com.jh.agendamento_service.domain.Agendamento;
import com.jh.agendamento_service.dto.AgendamentoRequest;
import com.jh.agendamento_service.dto.CategoriaResponse;
import com.jh.agendamento_service.dto.ProcedimentoResponse;
import com.jh.agendamento_service.dto.UsuarioAutenticadoDTO;
import com.jh.agendamento_service.enums.AgendamentoStatus;
import com.jh.agendamento_service.exception.ConflitoDeHorarioException;
import com.jh.agendamento_service.exception.ConflitoDeOperacaoException;
import com.jh.agendamento_service.exception.NaoAutorizadoException;
import com.jh.agendamento_service.exception.NaoEncontradoException;
import com.jh.agendamento_service.exception.ProcedimentoNaoDisponivelException;
import com.jh.agendamento_service.repository.AgendamentoCustomRepository;
import com.jh.agendamento_service.repository.AgendamentoRepository;

@ExtendWith(MockitoExtension.class)
public class AgendamentoServiceTest {

	private final static Long ID_DO_USUARIO = 1L;

	private final static String NOME_DO_USUARIO = "usuario";

	@InjectMocks
	private AgendamentoService agendamentoService;

	@Mock
	private AgendamentoRepository agendamentoRepository;

	@Mock
	private AgendamentoCustomRepository agendamentoCustomRepository;

	@Mock
	private ProcedimentoExternalService procedimentoExternalService;

	@Mock
	private SecurityContextHolder securityContextHolder;

	@Mock
	private SecurityService securityService;

	ArgumentCaptor<Agendamento> agendamentoCaptor;

	private ProcedimentoResponse procedimentoResponse;

	private CategoriaResponse categoriaResponse;

	private AgendamentoRequest agendamentoRequest;

	private Agendamento agendamento;

	private LocalDate data;

	private LocalTime inicio;

	private LocalTime fim;

	@BeforeEach
	public void setUp() {
		categoriaResponse = new CategoriaResponse(1L, "categoria", true);
		procedimentoResponse = new ProcedimentoResponse(1L, "titulo", "descrição", BigDecimal.TEN, 30, true,
				categoriaResponse);
		data = LocalDate.of(2026, 9, 19);
		inicio = LocalTime.of(2, 0);
		fim = inicio.plusMinutes(procedimentoResponse.duracaoEmMinutos());
		agendamentoRequest = new AgendamentoRequest(data, inicio, procedimentoResponse.id());

		agendamentoCaptor = ArgumentCaptor.forClass(Agendamento.class);
	}

	@Test
	public void deveCriarAgendamentoComSucesso() {
		configurarUsuarioAutenticado();
		when(procedimentoExternalService.procurarProcedimentoPorId(agendamentoRequest.procedimentoId()))
				.thenReturn(procedimentoResponse);

		when(agendamentoCustomRepository.existeConflitoDeHorario(data, inicio, fim, AgendamentoStatus.CANCELADO, null))
				.thenReturn(false);

		agendamentoService.criarAgendamento(agendamentoRequest);

		verify(agendamentoRepository, atLeastOnce()).save(agendamentoCaptor.capture());

		agendamento = agendamentoCaptor.getValue();

		assertEquals(agendamentoRequest.data(), agendamento.getData());
		assertEquals(agendamentoRequest.inicio(), agendamento.getInicio());
		assertEquals(agendamento.getInicio().plusMinutes(procedimentoResponse.duracaoEmMinutos()),
				agendamento.getFim());
		assertEquals(ID_DO_USUARIO, agendamento.getUsuarioId());
		assertEquals(NOME_DO_USUARIO, agendamento.getNomeDoUsuario());
		assertEquals(procedimentoResponse.titulo(), agendamento.getTituloDoProcedimento());
		assertEquals(procedimentoResponse.preco(), agendamento.getPreco());
		assertEquals(categoriaResponse.nome(), agendamento.getNomeDaCategoria());
		assertEquals(AgendamentoStatus.AGENDADO, agendamento.getStatus());
	}

	@Test
	public void deveLancarNaoEncontradoExceptionQuandoProcedimentoNaoForEncontradoAoCriarAgendamento() {
		configurarUsuarioAutenticado();
		when(procedimentoExternalService.procurarProcedimentoPorId(agendamentoRequest.procedimentoId()))
				.thenThrow(NaoEncontradoException.class);

		assertThrows(NaoEncontradoException.class, () -> agendamentoService.criarAgendamento(agendamentoRequest));

		verify(agendamentoRepository, never()).save(any());
	}

	@Test
	public void deveLancarConflitoDeHorarioExceptionQuandoJaExistirAgendamentoNaqueleIntervaloAoCriarAgendamento() {
		configurarUsuarioAutenticado();
		when(procedimentoExternalService.procurarProcedimentoPorId(agendamentoRequest.procedimentoId()))
				.thenReturn(procedimentoResponse);

		when(agendamentoCustomRepository.existeConflitoDeHorario(data, inicio, fim, AgendamentoStatus.CANCELADO, null))
				.thenReturn(true);

		assertThrows(ConflitoDeHorarioException.class, () -> agendamentoService.criarAgendamento(agendamentoRequest));

		verify(agendamentoRepository, never()).save(any());
	}

	@Test
	public void deveLancarProcedimentoNaoDisponivelExceptionQuandoAtivoDoProcedimentoForFalsoAoCriarAgendamento() {
		configurarUsuarioAutenticado();
		procedimentoResponse = new ProcedimentoResponse(1L, "titulo", "descrição", BigDecimal.TEN, 30, false,
				categoriaResponse);
		when(procedimentoExternalService.procurarProcedimentoPorId(agendamentoRequest.procedimentoId()))
				.thenReturn(procedimentoResponse);

		assertThrows(ProcedimentoNaoDisponivelException.class,
				() -> agendamentoService.criarAgendamento(agendamentoRequest));

		verify(agendamentoRepository, never()).save(any());
	}

	@Test
	public void deveProcurarAgendamentoPorIdComSucesso() {
		agendamento = criarAgendamento();
		when(agendamentoRepository.findById(agendamento.getId())).thenReturn(Optional.of(agendamento));

		agendamentoService.procurarAgendamentoPorId(agendamento.getId());

		verify(agendamentoRepository).findById(agendamento.getId());
	}

	@Test
	public void deveLancarNaoEncontradoExceptionQuandoAgendamentoNaoForencontradoAoProcurarAgendamentoPorId() {
		agendamento = criarAgendamento();
		when(agendamentoRepository.findById(agendamento.getId())).thenReturn(Optional.empty());

		assertThrows(NaoEncontradoException.class, () -> agendamentoService.procurarAgendamentoPorId(agendamento.getId()));

		verify(agendamentoRepository).findById(agendamento.getId());
	}

	@Test
	public void deveAtualizarAgendamentoComoClienteComSucesso() {
		configurarUsuarioAutenticado();
		agendamento = criarAgendamento();

		agendamentoRequest = new AgendamentoRequest(agendamento.getData().plusDays(1),
				agendamento.getInicio().plusMinutes(60), 2L);

		categoriaResponse = new CategoriaResponse(2L, "diferente", false);

		procedimentoResponse = new ProcedimentoResponse(2L, "diferente", "diferente", BigDecimal.ONE, 15, true,
				categoriaResponse);

		data = agendamentoRequest.data();
		inicio = agendamentoRequest.inicio();
		fim = inicio.plusMinutes(procedimentoResponse.duracaoEmMinutos());

		when(agendamentoRepository.findById(agendamento.getId())).thenReturn(Optional.of(agendamento));

		when(procedimentoExternalService.procurarProcedimentoPorId(agendamentoRequest.procedimentoId()))
				.thenReturn(procedimentoResponse);

		when(agendamentoCustomRepository.existeConflitoDeHorario(data, inicio, fim, AgendamentoStatus.CANCELADO,
				agendamento.getId())).thenReturn(false);

		agendamentoService.atualizarAgendamentoComoCliente(agendamento.getId(), agendamentoRequest);

		verify(agendamentoRepository, atLeastOnce()).save(agendamentoCaptor.capture());

		agendamento = agendamentoCaptor.getValue();

		assertEquals(agendamentoRequest.data(), agendamento.getData());
		assertEquals(agendamentoRequest.inicio(), agendamento.getInicio());
		assertEquals(agendamento.getInicio().plusMinutes(procedimentoResponse.duracaoEmMinutos()),
				agendamento.getFim());
		assertEquals(ID_DO_USUARIO, agendamento.getUsuarioId());
		assertEquals(NOME_DO_USUARIO, agendamento.getNomeDoUsuario());
		assertEquals(procedimentoResponse.titulo(), agendamento.getTituloDoProcedimento());
		assertEquals(procedimentoResponse.preco(), agendamento.getPreco());
		assertEquals(categoriaResponse.nome(), agendamento.getNomeDaCategoria());
		assertEquals(AgendamentoStatus.AGENDADO, agendamento.getStatus());
	}

	@Test
	public void deveLancarNaoEncontradoExceptionQuandoProcedimentoNaoForEncontradoAoAtualizarAgendamentoComoCliente() {
		agendamento = criarAgendamento();
		configurarUsuarioAutenticado();
		when(agendamentoRepository.findById(agendamento.getId())).thenReturn(Optional.of(agendamento));
		when(procedimentoExternalService.procurarProcedimentoPorId(agendamentoRequest.procedimentoId()))
				.thenThrow(NaoEncontradoException.class);

		assertThrows(NaoEncontradoException.class,
				() -> agendamentoService.atualizarAgendamentoComoCliente(agendamento.getId(), agendamentoRequest));

		verify(agendamentoRepository, never()).save(any());
	}

	@Test
	public void deveLancarConflitoDeHorarioExceptionQuandoJaExistirAgendamentoNaqueleIntervaloAoAtualizarAgendamentoComoCliente() {
		agendamento = criarAgendamento();
		configurarUsuarioAutenticado();
		when(agendamentoRepository.findById(agendamento.getId())).thenReturn(Optional.of(agendamento));
		when(procedimentoExternalService.procurarProcedimentoPorId(agendamentoRequest.procedimentoId()))
				.thenReturn(procedimentoResponse);

		when(agendamentoCustomRepository.existeConflitoDeHorario(data, inicio, fim, AgendamentoStatus.CANCELADO,
				agendamento.getId())).thenReturn(true);

		assertThrows(ConflitoDeHorarioException.class,
				() -> agendamentoService.atualizarAgendamentoComoCliente(agendamento.getId(), agendamentoRequest));

		verify(agendamentoRepository, never()).save(any());
	}

	@Test
	public void deveLancarProcedimentoNaoDisponivelExceptionQuandoAtivoDoProcedimentoForFalsoAoAtualizarAgendamentoComoCliente() {
		agendamento = criarAgendamento();
		configurarUsuarioAutenticado();
		when(agendamentoRepository.findById(agendamento.getId())).thenReturn(Optional.of(agendamento));
		procedimentoResponse = new ProcedimentoResponse(1L, "titulo", "descrição", BigDecimal.TEN, 30, false,
				categoriaResponse);
		when(procedimentoExternalService.procurarProcedimentoPorId(agendamentoRequest.procedimentoId()))
				.thenReturn(procedimentoResponse);

		assertThrows(ProcedimentoNaoDisponivelException.class,
				() -> agendamentoService.atualizarAgendamentoComoCliente(agendamento.getId(), agendamentoRequest));

		verify(agendamentoRepository, never()).save(any());
	}

	@Test
	public void deveLancarNaoAutorizadoExceptionQuandoForUmUsuarioDiferenteAoAtualizarAgendamentoComoCliente() {
		agendamento = criarAgendamento();
		agendamento.setUsuarioId(Long.MAX_VALUE);

		configurarUsuarioAutenticado();
		when(agendamentoRepository.findById(agendamento.getId())).thenReturn(Optional.of(agendamento));

		assertThrows(NaoAutorizadoException.class,
				() -> agendamentoService.atualizarAgendamentoComoCliente(agendamento.getId(), agendamentoRequest));

		verify(agendamentoRepository, never()).save(any());
	}

	@Test
	public void deveLancarNaoEncontradoExceptionQuandoAgendamentoNaoForEncontradoAoAtualizarAgendamentoComoCliente() {
		agendamento = criarAgendamento();

		when(agendamentoRepository.findById(agendamento.getId())).thenReturn(Optional.empty());

		assertThrows(NaoEncontradoException.class,
				() -> agendamentoService.atualizarAgendamentoComoCliente(agendamento.getId(), agendamentoRequest));

		verify(agendamentoRepository, never()).save(any());
	}

	@Test
	public void deveLancarConflitoDeOperacaoExceptionQuandoOInicioDoAgendamentoForMenorDoQue12HorasAtualizarAgendamentoComoCliente() {
		configurarUsuarioAutenticado();
		agendamento = criarAgendamento();
		agendamento.setData(LocalDate.now());

		when(agendamentoRepository.findById(agendamento.getId())).thenReturn(Optional.of(agendamento));
		when(procedimentoExternalService.procurarProcedimentoPorId(agendamentoRequest.procedimentoId()))
				.thenReturn(procedimentoResponse);

		assertThrows(ConflitoDeOperacaoException.class,
				() -> agendamentoService.atualizarAgendamentoComoCliente(agendamento.getId(), agendamentoRequest));

		verify(agendamentoRepository, never()).save(any());
	}

	@Test
	public void deveListarTodosOsHorariosDisponíveisDentroDoIntervalo() {
		LocalTime inicioDoExpediente = LocalTime.of(8, 0);
		LocalTime fimDoExpediente = LocalTime.of(18, 0);

		when(agendamentoRepository.findAllByData(data)).thenReturn(List.of());
		when(procedimentoExternalService.procurarProcedimentoPorId(procedimentoResponse.id()))
				.thenReturn(procedimentoResponse);

		List<LocalTime> horariosDisponiveis = agendamentoService.horariosDisponiveis(inicioDoExpediente,
				fimDoExpediente, data, procedimentoResponse.id());
		assertEquals(20, horariosDisponiveis.size());
	}

	@Test
	public void deveListarHorariosDisponiveisMenosHorarioQuePossuiConflito() {
		agendamento = criarAgendamento();
		LocalTime inicioDoExpediente = LocalTime.of(8, 0);
		LocalTime fimDoExpediente = LocalTime.of(18, 0);

		when(agendamentoRepository.findAllByData(data)).thenReturn(List.of(agendamento));
		when(procedimentoExternalService.procurarProcedimentoPorId(procedimentoResponse.id()))
				.thenReturn(procedimentoResponse);

		List<LocalTime> horariosDisponiveis = agendamentoService.horariosDisponiveis(inicioDoExpediente,
				fimDoExpediente, data, procedimentoResponse.id());
		assertEquals(19, horariosDisponiveis.size());
		assertFalse(horariosDisponiveis.contains(agendamento.getInicio()));
	}

	private void configurarUsuarioAutenticado() {
		UsuarioAutenticadoDTO usuarioAutenticado = new UsuarioAutenticadoDTO(ID_DO_USUARIO, NOME_DO_USUARIO);

		when(securityService.getUsuarioAutenticado()).thenReturn(usuarioAutenticado);
	}

	private Agendamento criarAgendamento() {
		LocalDateTime criadoEm = LocalDateTime.now();
		LocalDate data = LocalDate.now().plusDays(5);
		LocalTime inicio = LocalTime.of(14, 0);
		LocalTime fim = inicio.plusMinutes(procedimentoResponse.duracaoEmMinutos());

		Agendamento agendamento = new Agendamento("id", ID_DO_USUARIO, NOME_DO_USUARIO, criadoEm, data, inicio, fim,
				procedimentoResponse.titulo(), procedimentoResponse.duracaoEmMinutos(), procedimentoResponse.preco(),
				categoriaResponse.nome(), AgendamentoStatus.AGENDADO);

		return agendamento;
	}
}
