package com.jh.agendamento_service.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jh.agendamento_service.domain.Agendamento;
import com.jh.agendamento_service.dto.AgendamentoAdminRequest;
import com.jh.agendamento_service.dto.CategoriaResponse;
import com.jh.agendamento_service.dto.ProcedimentoResponse;
import com.jh.agendamento_service.enums.AgendamentoStatus;
import com.jh.agendamento_service.repository.AgendamentoRepository;
import com.jh.agendamento_service.service.ProcedimentoExternalService;

import jakarta.transaction.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AgendamentoAdminControllerTest {
	
	private final String BASE_URL = "/agendamento/admin";
	
	private final String admin = "SCOPE_ADMIN";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockitoBean
	private ProcedimentoExternalService procedimentoExternalService;
	
	@Autowired
	private AgendamentoRepository agendamentoRepository;
	
	private AgendamentoAdminRequest agendamentoAdminRequest;
	
	private LocalDate data;
	
	private LocalTime horario;
	
	@BeforeEach
	public void setUp() {
		agendamentoRepository.deleteAll();
		
		data = LocalDate.now().plusDays(2);
		horario = LocalTime.of(14, 0);
		ProcedimentoResponse procedimentoResponse = new ProcedimentoResponse(1L, "titulo", "descrição", BigDecimal.TEN,
				30, true, new CategoriaResponse(1L, "nome", true));
		when(procedimentoExternalService.procurarProcedimentoPorId(1L)).thenReturn(procedimentoResponse);
		
		agendamentoAdminRequest = new AgendamentoAdminRequest(1L, "usuario", data, horario, AgendamentoStatus.AGENDADO, 1L);
	}
	
	@Test
	@WithMockUser(authorities = admin)
	public void deveCriarAgendamentoComSucesso() throws JacksonException, Exception {
		
		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoAdminRequest)))
		.andExpect(status().isCreated());
		
		List<Agendamento> agendamentos = agendamentoRepository.findAll();
		
		assertEquals(1, agendamentos.size());
	}
}
