package com.jh.agendamento_service.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jh.agendamento_service.domain.Agendamento;
import com.jh.agendamento_service.dto.AgendamentoAdminRequest;
import com.jh.agendamento_service.dto.AgendamentoResponse;
import com.jh.agendamento_service.dto.AgendamentoStatusRequest;
import com.jh.agendamento_service.dto.CategoriaResponse;
import com.jh.agendamento_service.dto.ProcedimentoResponse;
import com.jh.agendamento_service.enums.AgendamentoStatus;
import com.jh.agendamento_service.exception.ConflitoDeHorarioException;
import com.jh.agendamento_service.exception.NaoEncontradoException;
import com.jh.agendamento_service.repository.AgendamentoCustomRepository;
import com.jh.agendamento_service.repository.AgendamentoRepository;
import com.jh.agendamento_service.service.AgendamentoAdminService;
import com.jh.agendamento_service.service.ProcedimentoExternalService;

@ExtendWith(MockitoExtension.class)
public class AgendamentoAdminServiceTest {
		
	@InjectMocks
	private AgendamentoAdminService agendamentoAdminService;
	
	@Mock
	private AgendamentoRepository agendamentoRepository;
	
	@Mock
	private AgendamentoCustomRepository agendamentoCustomRepository;
	
	@Mock
	private ProcedimentoExternalService procedimentoExternalService;
	
	private AgendamentoAdminRequest agendamentoAdminRequest;
	
	private AgendamentoResponse agendamentoResponse;
	
	private ProcedimentoResponse procedimentoResponse;
	
	private CategoriaResponse categoriaResponse;
		
	private LocalDate data;
	
	private LocalTime horario;
	
	private ArgumentCaptor<Agendamento> captor;
	
	private Agendamento agendamento;
	
	@BeforeEach
	public void setUp() {
		data = LocalDate.of(2026, 8, 19);
		horario = LocalTime.of(2, 0);
		categoriaResponse = new CategoriaResponse(1L, "categoria", true);
		procedimentoResponse = new ProcedimentoResponse(1L, "titulo", "descrição", BigDecimal.TEN, 30, true, categoriaResponse);
		agendamentoAdminRequest = new AgendamentoAdminRequest(1L, "usuario", data, horario, AgendamentoStatus.CONCLUIDO, procedimentoResponse.id());
		agendamento = inicializarAgendamento();
		captor = ArgumentCaptor.forClass(Agendamento.class);
	}
	
	@Test
	public void deveCriarAgendamentoComSucesso() {
		when(procedimentoExternalService.procurarProcedimentoPorId(agendamentoAdminRequest.procedimentoId())).thenReturn(procedimentoResponse);
		
		agendamentoAdminService.criarAgendamento(agendamentoAdminRequest);
		
		verify(agendamentoRepository).save(captor.capture());
		
		Agendamento agendamentoSalvo = captor.getValue();
		
		assertEquals(agendamentoAdminRequest.usuarioId(), agendamentoSalvo.getUsuarioId());
		assertEquals(agendamentoAdminRequest.nomeDoUsuario(), agendamentoSalvo.getNomeDoUsuario());
		//assertEquals(LocalDateTime.now(), agendamentoSalvo.getCriadoEm());
		assertEquals(agendamentoAdminRequest.data(), agendamentoSalvo.getData());
		assertEquals(agendamentoAdminRequest.inicio(), agendamentoSalvo.getInicio());
		assertEquals(agendamentoAdminRequest.inicio().plusMinutes(procedimentoResponse.duracaoEmMinutos()), agendamentoSalvo.getFim());
		assertEquals(agendamentoAdminRequest.status(), agendamentoSalvo.getStatus());
		assertEquals(procedimentoResponse.titulo(), agendamentoSalvo.getTituloDoProcedimento());
		assertEquals(procedimentoResponse.duracaoEmMinutos(), agendamentoSalvo.getDuracaoEmMinutos());
		assertEquals(procedimentoResponse.preco(), agendamentoSalvo.getPreco());
		assertEquals(categoriaResponse.nome(), agendamentoSalvo.getNomeDaCategoria());
	}
	
	@Test
	public void deveLancarConflitoDeHorarioExceptionQuandoOAgendamentoResultadoEmConflitoAoCriarAgendamento() {
		when(procedimentoExternalService.procurarProcedimentoPorId(agendamentoAdminRequest.procedimentoId())).thenReturn(procedimentoResponse);
		when(agendamentoCustomRepository.existeConflitoDeHorario(any(), any(), any(), any())).thenReturn(true);
		
		assertThrows(ConflitoDeHorarioException.class, () -> agendamentoAdminService.criarAgendamento(agendamentoAdminRequest));
		
		verify(agendamentoRepository, never()).save(captor.capture());
	}
	
	@Test
	public void deveRetornarOAgendamentoPorIdComSucesso() {
		when(agendamentoRepository.findById("id")).thenReturn(Optional.of(agendamento));
		
		agendamentoResponse = agendamentoAdminService.getAgendamentoPorId("id");
				
		assertEquals(agendamentoResponse.status(), agendamento.getStatus());
		
		assertEquals(agendamentoResponse.usuarioId(), agendamento.getUsuarioId());
		assertEquals(agendamentoResponse.nomeDoUsuario(), agendamento.getNomeDoUsuario());
		assertEquals(agendamentoResponse.data(), agendamento.getData());
		assertEquals(agendamentoResponse.inicio(), agendamento.getInicio());
		assertEquals(agendamentoResponse.fim(), agendamento.getFim());
		assertEquals(agendamentoResponse.status(), agendamento.getStatus());
		assertEquals(agendamentoResponse.tituloDoProcedimento(), agendamento.getTituloDoProcedimento());
		assertEquals(agendamentoResponse.duracaoEmMinutos(), agendamento.getDuracaoEmMinutos());
		assertEquals(agendamentoResponse.preco(), agendamento.getPreco());
		assertEquals(agendamentoResponse.nomeDaCategoria(), agendamento.getNomeDaCategoria());
	}
	
	@Test
	public void deveLancarNaoEncontradoExceptionQuandoAgendamentoNaoForEncontradoAoGetAgendamentoPorId() {
		when(agendamentoRepository.findById("id")).thenReturn(Optional.empty());
		
		assertThrows(NaoEncontradoException.class, () -> agendamentoAdminService.getAgendamentoPorId("id"));
		verify(agendamentoRepository, never()).save(any());
	}
	
	@Test
	public void deveAtualizarAgendamentoComSucesso() {
		when(procedimentoExternalService.procurarProcedimentoPorId(agendamentoAdminRequest.procedimentoId())).thenReturn(procedimentoResponse);
		when(agendamentoRepository.findById("id")).thenReturn(Optional.of(agendamento));
		
		agendamentoAdminService.atualizarAgendamento("id", agendamentoAdminRequest);
		
		verify(agendamentoRepository).save(captor.capture());
		
		Agendamento agendamentoSalvo = captor.getValue();
		
		assertEquals(agendamentoAdminRequest.usuarioId(), agendamentoSalvo.getUsuarioId());
		assertEquals(agendamentoAdminRequest.nomeDoUsuario(), agendamentoSalvo.getNomeDoUsuario());
		//assertEquals(LocalDateTime.now(), agendamentoSalvo.getCriadoEm());
		assertEquals(agendamentoAdminRequest.data(), agendamentoSalvo.getData());
		assertEquals(agendamentoAdminRequest.inicio(), agendamentoSalvo.getInicio());
		assertEquals(agendamentoAdminRequest.inicio().plusMinutes(procedimentoResponse.duracaoEmMinutos()), agendamentoSalvo.getFim());
		assertEquals(agendamentoAdminRequest.status(), agendamentoSalvo.getStatus());
		assertEquals(procedimentoResponse.titulo(), agendamentoSalvo.getTituloDoProcedimento());
		assertEquals(procedimentoResponse.duracaoEmMinutos(), agendamentoSalvo.getDuracaoEmMinutos());
		assertEquals(procedimentoResponse.preco(), agendamentoSalvo.getPreco());
		assertEquals(categoriaResponse.nome(), agendamentoSalvo.getNomeDaCategoria());
	}
	
	@Test
	public void deveLancarConflitoDeHorarioExceptionQuandoOAgendamentoResultadoEmConflitoAoAtualizarAgendamento() {		
		when(agendamentoRepository.findById("id")).thenReturn(Optional.of(agendamento));
		when(procedimentoExternalService.procurarProcedimentoPorId(agendamentoAdminRequest.procedimentoId())).thenReturn(procedimentoResponse);
		when(agendamentoCustomRepository.existeConflitoDeHorario(any(), any(), any(), any())).thenReturn(true);
		
		assertThrows(ConflitoDeHorarioException.class, () -> agendamentoAdminService.atualizarAgendamento("id", agendamentoAdminRequest));
		
		verify(agendamentoRepository, never()).save(captor.capture());
	}
	
	@Test
	public void deveAlterarStatusDoAgendamentoComSucesso() {
		AgendamentoStatusRequest agendamentoStatusRequest = new AgendamentoStatusRequest(AgendamentoStatus.CONCLUIDO);
		when(agendamentoRepository.findById("id")).thenReturn(Optional.of(agendamento));
		
		agendamentoAdminService.alterarStatusDoAgendamento("id", agendamentoStatusRequest);
		
		verify(agendamentoRepository).save(captor.capture());
		
		agendamento = captor.getValue();
		assertEquals(agendamentoStatusRequest.status(), agendamento.getStatus());
	}
	
	@Test
	public void deveLancarNaoEncontradoExceptionQuandoAgendamentoNaoForEncontradoAoAlterarStatusDoAgendamento() {
		AgendamentoStatusRequest agendamentoStatusRequest = new AgendamentoStatusRequest(AgendamentoStatus.CONCLUIDO);
		when(agendamentoRepository.findById("id")).thenReturn(Optional.empty());
		
		assertThrows(NaoEncontradoException.class, () -> agendamentoAdminService.alterarStatusDoAgendamento("id", agendamentoStatusRequest));
		verify(agendamentoRepository, never()).save(any());
	}
	
	private Agendamento inicializarAgendamento() {
		Agendamento agendamento = new Agendamento();
		agendamento.setData(LocalDate.now());
		agendamento.setInicio(LocalTime.of(23, 0));
		agendamento.setInicio(LocalTime.of(23, 50));
		agendamento.setStatus(AgendamentoStatus.AGENDADO);
		agendamento.setUsuarioId(9L);
		agendamento.setNomeDoUsuario("usuario diferente");
		agendamento.setTituloDoProcedimento("titulo diferente");
		agendamento.setPreco(BigDecimal.ZERO);
		agendamento.setDuracaoEmMinutos(50);
		agendamento.setNomeDaCategoria("categoria diferente");
		
		return agendamento;
	}
}
