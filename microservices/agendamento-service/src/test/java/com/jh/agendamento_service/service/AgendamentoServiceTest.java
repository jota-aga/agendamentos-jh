package com.jh.agendamento_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import com.jh.agendamento_service.domain.Agendamento;
import com.jh.agendamento_service.dto.AgendamentoRequest;
import com.jh.agendamento_service.dto.CategoriaResponse;
import com.jh.agendamento_service.dto.ProcedimentoResponse;
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
	private ProcedimentoExternalService procedimentoExternalService;

	@Mock
	private SecurityContextHolder securityContextHolder;

	@Mock
	private SecurityContext securityContext;

	@Mock
	private Authentication authentication;

	@Mock
	private Jwt jwt;
	
	ArgumentCaptor<Agendamento> agendamentoCaptor;

	private ProcedimentoResponse procedimentoResponse;

	private CategoriaResponse categoriaResponse;

	private AgendamentoRequest agendamentoRequest;
	
	private Agendamento agendamento;

	@BeforeEach
	public void setUp() {
		categoriaResponse = new CategoriaResponse(1L, "categoria", true);
		procedimentoResponse = new ProcedimentoResponse(1L, "titulo", "descrição", BigDecimal.TEN, 30, true,
				categoriaResponse);
		agendamentoRequest = new AgendamentoRequest(LocalDate.now(), LocalTime.now(), procedimentoResponse.id());

		SecurityContextHolder.getContext().setAuthentication(authentication);

		when(authentication.getPrincipal()).thenReturn(jwt);

		when(jwt.getSubject()).thenReturn(ID_DO_USUARIO.toString());

		when(jwt.getClaimAsString("nome")).thenReturn(NOME_DO_USUARIO);
		
		agendamentoCaptor = ArgumentCaptor.forClass(Agendamento.class);
	}

	@Test
	public void deveCriarAgendamentoComSucesso() {
		when(procedimentoExternalService.procurarProcedimentoPorId(agendamentoRequest.procedimentoId()))
				.thenReturn(procedimentoResponse);
		
		when(agendamentoRepository.existsByDataAndInicioLessThanAndFimGreaterThan(any(), any(), any()))
				.thenReturn(false);
		
		agendamentoService.criarAgendamento(agendamentoRequest);

		verify(agendamentoRepository, atLeastOnce()).save(agendamentoCaptor.capture());
		
		agendamento = agendamentoCaptor.getValue();
	
		assertEquals(agendamento.getData(), agendamentoRequest.data());
		assertEquals(agendamento.getInicio(), agendamentoRequest.inicio());
		assertEquals(agendamento.getFim(), agendamento.getInicio().plusMinutes(procedimentoResponse.duracaoEmMinutos()));
		assertEquals(agendamento.getUsuarioId(), ID_DO_USUARIO);
		assertEquals(agendamento.getNomeDoUsuario(), NOME_DO_USUARIO);
		assertEquals(agendamento.getTituloDoProcedimento(), procedimentoResponse.titulo());
		assertEquals(agendamento.getPreco(), procedimentoResponse.preco());
		assertEquals(agendamento.getNomeDaCategoria(), categoriaResponse.nome());
	}
}
