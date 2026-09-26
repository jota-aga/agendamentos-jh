package com.jh.agendamento_service.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.jh.agendamento_service.domain.Agendamento;
import com.jh.agendamento_service.dto.AgendamentoRequest;
import com.jh.agendamento_service.dto.CategoriaResponse;
import com.jh.agendamento_service.dto.ProcedimentoResponse;
import com.jh.agendamento_service.dto.UsuarioAutenticadoDTO;
import com.jh.agendamento_service.repository.AgendamentoRepository;
import com.jh.agendamento_service.service.ProcedimentoExternalService;
import com.jh.agendamento_service.service.SecurityService;

import jakarta.transaction.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AgendamentoControllerTest {
	
	private final String BASE_URL = "/agendamento";
	
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AgendamentoRepository agendamentoRepository;

	@MockitoBean
	private ProcedimentoExternalService procedimentoExternalService;
	
	@MockitoBean
	private SecurityService securityService;
	
	private AgendamentoRequest agendamentoRequest;
	
	private LocalDate data;
	
	private LocalTime horario;

	@BeforeEach
	public void setUp() {
		agendamentoRepository.deleteAll();
		
		ProcedimentoResponse procedimentoResponse = new ProcedimentoResponse(1L, "titulo", "descrição", BigDecimal.TEN,
				30, true, new CategoriaResponse(1L, "nome", true));
		when(procedimentoExternalService.procurarProcedimentoPorId(1L)).thenReturn(procedimentoResponse);
		data = LocalDate.of(2026, 9, 18);
		horario = LocalTime.of(2, 0);
		agendamentoRequest = new AgendamentoRequest(data, horario, procedimentoResponse.id());
		
		UsuarioAutenticadoDTO autenticadoDTO = new UsuarioAutenticadoDTO(1L, "usuario");
		when(securityService.getUsuarioAutenticado()).thenReturn(autenticadoDTO);
	}

	@Test
	@WithMockUser
	public void deveCriarAgendamentoComSucesso() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest)))
		.andExpect(status().isCreated());
		
		List<Agendamento> agendamentosSalvos = agendamentoRepository.findAll();
		
		assertEquals(1, agendamentosSalvos.size());
	}
}
