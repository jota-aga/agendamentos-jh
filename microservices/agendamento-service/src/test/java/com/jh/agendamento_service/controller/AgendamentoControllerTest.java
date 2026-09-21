package com.jh.agendamento_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.jh.agendamento_service.config.SecurityConfig;
import com.jh.agendamento_service.dto.AgendamentoRequest;
import com.jh.agendamento_service.exception.ConflitoDeHorarioException;
import com.jh.agendamento_service.exception.NaoEncontradoException;
import com.jh.agendamento_service.service.AgendamentoService;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = AgendamentoController.class)
@Import(SecurityConfig.class)
public class AgendamentoControllerTest {
	private static final String BASE_URL = "/agendamento";
	
	private static final String ADMIN = "SCOPE_ADMIN";
		
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockitoBean
	private AgendamentoService agendamentoService;
	
	private AgendamentoRequest agendamentoRequest;
	
	private LocalDate data;
	
	private LocalTime horario;
	
	@BeforeEach
	public void setUp() {
		data = LocalDate.of(2026, 9, 18);
		horario = LocalTime.of(14, 0);
		agendamentoRequest = new AgendamentoRequest(data, horario, 1L);
	}
	
	@Test
	@WithMockUser
	public void deveRetornar201AoCriarAgendamento() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest)))
		.andExpect(status().isCreated());
		
		verify(agendamentoService).criarAgendamento(agendamentoRequest);
	}
	
	@Test
	@WithMockUser
	public void deveRetornar400QuandoRequestForInvalidoAoCriarAgendamento() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new AgendamentoRequest(null, null, null))))
		.andExpect(status().isBadRequest());
		
		verify(agendamentoService, never()).criarAgendamento(any());
	}
	
	@Test
	@WithMockUser
	public void deveRetornar404QuandoProcedimentoNaoForEncontradoAoCriarAgendamento() throws JacksonException, Exception {
		NaoEncontradoException exception = new NaoEncontradoException("Procedimento");
		doThrow(exception).when(agendamentoService).criarAgendamento(agendamentoRequest);
		
		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest)))
		.andExpect(status().isNotFound())
		.andExpect(content().string(exception.getMessage()));
	}
	
	@Test
	@WithMockUser
	public void deveRetornar409QuandoAgendamentoResultarEmConflitoAoCriarAgendamento() throws JacksonException, Exception {
		ConflitoDeHorarioException exception = new ConflitoDeHorarioException();
		doThrow(exception).when(agendamentoService).criarAgendamento(agendamentoRequest);
		
		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest)))
		.andExpect(status().isConflict())
		.andExpect(content().string(exception.getMessage()));
	}
	
	@Test
	@WithMockUser
	public void deveRetornar200AoAtualizarAgendamentoComoCliente() throws JacksonException, Exception {
		mockMvc.perform(put(BASE_URL+"/id")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest)))
		.andExpect(status().isOk());
		
		verify(agendamentoService).atualizarAgendamentoComoCliente("id", agendamentoRequest);
	}
	
	@Test
	@WithMockUser
	public void deveRetornar404QuandoAgendamentoNaoForEncontradoAoAtualizarAgendamentoComoCliente() throws JacksonException, Exception {
		NaoEncontradoException exception = new NaoEncontradoException("Agendamento");
		doThrow(exception).when(agendamentoService).atualizarAgendamentoComoCliente("id", agendamentoRequest);
		
		mockMvc.perform(put(BASE_URL+"/id")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest)))
		.andExpect(status().isNotFound())
		.andExpect(content().string(exception.getMessage()));
		
		verify(agendamentoService).atualizarAgendamentoComoCliente("id", agendamentoRequest);

	}
	
	@Test
	@WithMockUser
	public void deveRetornar400QuandoRequestForInvalidoAoAtualizarAgendamentoComoCliente() throws JacksonException, Exception {
		mockMvc.perform(put(BASE_URL+"/id")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new AgendamentoRequest(null, null, null))))
		.andExpect(status().isBadRequest());
		
		verify(agendamentoService, never()).atualizarAgendamentoComoCliente("id", agendamentoRequest);
	}
	
	@Test
	@WithMockUser
	public void deveRetornar404QuandoProcedimentoNaoForEncontradoAoAtualizarAgendamento() throws JacksonException, Exception {
		NaoEncontradoException exception = new NaoEncontradoException("Procedimento");
		doThrow(exception).when(agendamentoService).atualizarAgendamentoComoCliente("id", agendamentoRequest);
		
		mockMvc.perform(put(BASE_URL+"/id")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest)))
		.andExpect(status().isNotFound())
		.andExpect(content().string(exception.getMessage()));
	}
	
	@Test
	@WithMockUser
	public void deveRetornar409QuandoAgendamentoResultarEmConflitoAoAtualizarAgendamentoComoCliente() throws JacksonException, Exception {
		ConflitoDeHorarioException exception = new ConflitoDeHorarioException();
		doThrow(exception).when(agendamentoService).atualizarAgendamentoComoCliente("id", agendamentoRequest);
		
		mockMvc.perform(put(BASE_URL+"/id")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest)))
		.andExpect(status().isConflict())
		.andExpect(content().string(exception.getMessage()));
	}
}
