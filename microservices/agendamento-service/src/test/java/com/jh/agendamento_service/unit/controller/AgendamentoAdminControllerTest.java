package com.jh.agendamento_service.unit.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.jh.agendamento_service.controller.AgendamentoAdminController;
import com.jh.agendamento_service.dto.AgendamentoAdminRequest;
import com.jh.agendamento_service.dto.AgendamentoResponse;
import com.jh.agendamento_service.enums.AgendamentoStatus;
import com.jh.agendamento_service.exception.ConflitoDeHorarioException;
import com.jh.agendamento_service.infra.SecurityConfig;
import com.jh.agendamento_service.service.AgendamentoAdminService;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AgendamentoAdminController.class)
@Import(SecurityConfig.class)
public class AgendamentoAdminControllerTest {
	
	private static final String BASE_URL = "/agendamento/admin";
	
	private static final String AGENDAMENTO_ID = "id";
	
	private String scope = "admin";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockitoBean
	private AgendamentoAdminService agendamentoAdminService;
	
	private LocalDate data;
	
	private LocalTime horario;
	
	private AgendamentoAdminRequest agendamentoAdminRequest;
	
	private AgendamentoResponse agendamentoResponse;
	
	@BeforeEach
	public void setUp() {
		data = LocalDate.of(2026, 8, 18);
		horario = LocalTime.of(14, 0);
		agendamentoAdminRequest = new AgendamentoAdminRequest(1L, "usuario", data, horario, AgendamentoStatus.CONCLUIDO, 1L);
		agendamentoResponse = new AgendamentoResponse(AGENDAMENTO_ID, 1L, "usuario", LocalDateTime.now(), data, horario,
				horario.plusMinutes(30), "procedimento", 30, BigDecimal.TEN, "categoria", AgendamentoStatus.AGENDADO);
	}
	
	public void deveRetornar200AoCriarAgendamentoComSucesso() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoAdminRequest)))
		.andExpect(status().isOk());
		
		verify(agendamentoAdminService).criarAgendamento(agendamentoAdminRequest);
	}
	
	public void deveRetornar400QuandoRequestForIncorretoAoCriarAgendamento() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new AgendamentoAdminRequest(null, null, null, null, null, null))))
		.andExpect(status().isBadRequest());
		
		verify(agendamentoAdminService, never()).criarAgendamento(agendamentoAdminRequest);
	}
	
	public void deveRetornar409QuandoAgendamentoResultarEmConflitoDeHorarioAoCriarAgendamento() throws JacksonException, Exception {
		doThrow(ConflitoDeHorarioException.class).when(agendamentoAdminService).criarAgendamento(agendamentoAdminRequest);
		
		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new AgendamentoAdminRequest(null, null, null, null, null, null))))
		.andExpect(status().isConflict());
		
		verify(agendamentoAdminService).criarAgendamento(agendamentoAdminRequest);
	}
}
