package com.jh.agendamento_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

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
import com.jh.agendamento_service.dto.CategoriaResponse;
import com.jh.agendamento_service.dto.ProcedimentoResponse;
import com.jh.agendamento_service.dto.UsuarioAutenticadoDTO;
import com.jh.agendamento_service.enums.AgendamentoStatus;
import com.jh.agendamento_service.exception.ConflitoDeHorarioException;
import com.jh.agendamento_service.exception.ProcedimentoNaoDisponivelException;
import com.jh.agendamento_service.repository.AgendamentoCustomRepository;
import com.jh.agendamento_service.repository.AgendamentoRepository;

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
	
	private UsuarioAutenticadoDTO autenticadoDTO;
	
	private LocalDate data;
	
	private LocalTime horario;
	
	private ArgumentCaptor<Agendamento> captor;
	
	@BeforeEach
	public void setUp() {
		data = LocalDate.of(2026, 8, 19);
		horario = LocalTime.of(2, 0);
		categoriaResponse = new CategoriaResponse(1L, "categoria", true);
		procedimentoResponse = new ProcedimentoResponse(1L, "titulo", "descrição", BigDecimal.TEN, 30, true, categoriaResponse);
		agendamentoAdminRequest = new AgendamentoAdminRequest(1L, "usuario", data, horario, AgendamentoStatus.CONCLUIDO, procedimentoResponse.id());
		captor = ArgumentCaptor.forClass(Agendamento.class);
	}
	
	@Test
	public void deveSalvarAgendamentoComSucesso() {
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
	public void deveLancarConflitoDeHorarioExceptionQuandoOAgendamentoResultadoEmConflitoAo() {
		when(procedimentoExternalService.procurarProcedimentoPorId(agendamentoAdminRequest.procedimentoId())).thenReturn(procedimentoResponse);
		when(agendamentoCustomRepository.existeConflitoDeHorario(any(), any(), any(), any())).thenReturn(true);
		
		assertThrows(ConflitoDeHorarioException.class, () -> agendamentoAdminService.criarAgendamento(agendamentoAdminRequest));
		
		verify(agendamentoRepository, never()).save(captor.capture());
	}
}
