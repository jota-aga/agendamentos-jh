package com.jh.agendamento_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.jh.agendamento_service.dto.AgendamentoResponse;
import com.jh.agendamento_service.dto.AgendamentoStatusRequest;
import com.jh.agendamento_service.enums.AgendamentoStatus;
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
	
	private static final String AGENDAMENTO_ID = "id";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private AgendamentoService agendamentoService;

	private AgendamentoRequest agendamentoRequest;

	private AgendamentoResponse agendamentoResponse;

	private LocalDate data;

	private LocalTime horario;

	@BeforeEach
	public void setUp() {
		data = LocalDate.of(2026, 9, 18);
		horario = LocalTime.of(14, 0);
		agendamentoRequest = new AgendamentoRequest(data, horario, 1L);
		agendamentoResponse = new AgendamentoResponse(AGENDAMENTO_ID, 1L, "usuario", LocalDateTime.now(), data, horario,
				horario.plusMinutes(30), "procedimento", 30, BigDecimal.TEN, "categoria", AgendamentoStatus.AGENDADO);

	}

	@Test
	@WithMockUser
	public void deveRetornar201AoCriarAgendamento() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest))).andExpect(status().isCreated());

		verify(agendamentoService).criarAgendamento(agendamentoRequest);
	}

	@Test
	@WithMockUser
	public void deveRetornar400QuandoRequestForInvalidoAoCriarAgendamento() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new AgendamentoRequest(null, null, null))))
				.andExpect(status().isBadRequest());

		verify(agendamentoService, never()).criarAgendamento(any());
	}

	@Test
	@WithMockUser
	public void deveRetornar404QuandoProcedimentoNaoForEncontradoAoCriarAgendamento()
			throws JacksonException, Exception {
		NaoEncontradoException exception = new NaoEncontradoException("Procedimento");
		doThrow(exception).when(agendamentoService).criarAgendamento(agendamentoRequest);

		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest))).andExpect(status().isNotFound())
				.andExpect(content().string(exception.getMessage()));
	}

	@Test
	@WithMockUser
	public void deveRetornar409QuandoAgendamentoResultarEmConflitoAoCriarAgendamento()
			throws JacksonException, Exception {
		ConflitoDeHorarioException exception = new ConflitoDeHorarioException();
		doThrow(exception).when(agendamentoService).criarAgendamento(agendamentoRequest);

		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest))).andExpect(status().isConflict())
				.andExpect(content().string(exception.getMessage()));
	}

	@Test
	@WithMockUser
	public void deveRetornar200AoAtualizarAgendamentoComoCliente() throws JacksonException, Exception {
		mockMvc.perform(put(BASE_URL + "/id").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest))).andExpect(status().isOk());

		verify(agendamentoService).atualizarAgendamentoComoCliente(AGENDAMENTO_ID, agendamentoRequest);
	}

	@Test
	@WithMockUser
	public void deveRetornar404QuandoAgendamentoNaoForEncontradoAoAtualizarAgendamentoComoCliente()
			throws JacksonException, Exception {
		NaoEncontradoException exception = new NaoEncontradoException("Agendamento");
		doThrow(exception).when(agendamentoService).atualizarAgendamentoComoCliente(AGENDAMENTO_ID, agendamentoRequest);

		mockMvc.perform(put(BASE_URL + "/id").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest))).andExpect(status().isNotFound())
				.andExpect(content().string(exception.getMessage()));

		verify(agendamentoService).atualizarAgendamentoComoCliente(AGENDAMENTO_ID, agendamentoRequest);

	}

	@Test
	@WithMockUser
	public void deveRetornar400QuandoRequestForInvalidoAoAtualizarAgendamentoComoCliente()
			throws JacksonException, Exception {
		mockMvc.perform(put(BASE_URL + "/id").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new AgendamentoRequest(null, null, null))))
				.andExpect(status().isBadRequest());

		verify(agendamentoService, never()).atualizarAgendamentoComoCliente(AGENDAMENTO_ID, agendamentoRequest);
	}

	@Test
	@WithMockUser
	public void deveRetornar404QuandoProcedimentoNaoForEncontradoAoAtualizarAgendamentoComoCliente()
			throws JacksonException, Exception {
		NaoEncontradoException exception = new NaoEncontradoException("Procedimento");
		doThrow(exception).when(agendamentoService).atualizarAgendamentoComoCliente(AGENDAMENTO_ID, agendamentoRequest);

		mockMvc.perform(put(BASE_URL + "/id").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest))).andExpect(status().isNotFound())
				.andExpect(content().string(exception.getMessage()));
	}

	@Test
	@WithMockUser
	public void deveRetornar409QuandoAgendamentoResultarEmConflitoAoAtualizarAgendamentoComoCliente()
			throws JacksonException, Exception {
		ConflitoDeHorarioException exception = new ConflitoDeHorarioException();
		doThrow(exception).when(agendamentoService).atualizarAgendamentoComoCliente(AGENDAMENTO_ID, agendamentoRequest);

		mockMvc.perform(put(BASE_URL + "/id").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest))).andExpect(status().isConflict())
				.andExpect(content().string(exception.getMessage()));
	}
	
	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar200AoAtualizarAgendamentoComoAdmin() throws JacksonException, Exception {
		mockMvc.perform(put(BASE_URL + "/id/admin").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest))).andExpect(status().isOk());

		verify(agendamentoService).atualizarAgendamentoComoAdmin(AGENDAMENTO_ID, agendamentoRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar404QuandoAgendamentoNaoForEncontradoAoAtualizarAgendamentoComoAdmin()
			throws JacksonException, Exception {
		NaoEncontradoException exception = new NaoEncontradoException("Agendamento");
		doThrow(exception).when(agendamentoService).atualizarAgendamentoComoAdmin(AGENDAMENTO_ID, agendamentoRequest);

		mockMvc.perform(put(BASE_URL + "/id/admin").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest))).andExpect(status().isNotFound())
				.andExpect(content().string(exception.getMessage()));

		verify(agendamentoService).atualizarAgendamentoComoAdmin(AGENDAMENTO_ID, agendamentoRequest);

	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar400QuandoRequestForInvalidoAoAtualizarAgendamentoComoAdmin()
			throws JacksonException, Exception {
		mockMvc.perform(put(BASE_URL + "/id/admin").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new AgendamentoRequest(null, null, null))))
				.andExpect(status().isBadRequest());

		verify(agendamentoService, never()).atualizarAgendamentoComoAdmin(AGENDAMENTO_ID, agendamentoRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar404QuandoProcedimentoNaoForEncontradoAoAtualizarAgendamentoComoAdmin()
			throws JacksonException, Exception {
		NaoEncontradoException exception = new NaoEncontradoException("Procedimento");
		doThrow(exception).when(agendamentoService).atualizarAgendamentoComoCliente(AGENDAMENTO_ID, agendamentoRequest);

		mockMvc.perform(put(BASE_URL + "/id").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest))).andExpect(status().isNotFound())
				.andExpect(content().string(exception.getMessage()))
				.andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar409QuandoHorarioDoAgendamentoResultarEmConflitoAoAtualizarAgendamentoComoAdmin()
			throws JacksonException, Exception {
		ConflitoDeHorarioException exception = new ConflitoDeHorarioException();
		doThrow(exception).when(agendamentoService).atualizarAgendamentoComoAdmin(AGENDAMENTO_ID, agendamentoRequest);

		mockMvc.perform(put(BASE_URL + "/id/admin").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest))).andExpect(status().isConflict())
				.andExpect(content().string(exception.getMessage()))
				.andExpect(status().isConflict());
	}
	
	@Test
	@WithMockUser
	public void deveRetornar403QuandoUsuarioNaoAdminTentarAtualizarAgendamentoComoAdmin()
			throws JacksonException, Exception {
		ConflitoDeHorarioException exception = new ConflitoDeHorarioException();
		doThrow(exception).when(agendamentoService).atualizarAgendamentoComoAdmin(AGENDAMENTO_ID, agendamentoRequest);

		mockMvc.perform(put(BASE_URL + "/id/admin").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest)))
				.andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar200QuandoAtualizarStatusDoAgendamento() throws JacksonException, Exception {
		AgendamentoStatusRequest statusRequest = new AgendamentoStatusRequest(AgendamentoStatus.CANCELADO);
		
		mockMvc.perform(patch(BASE_URL+"/id/status/admin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(statusRequest)))
		.andExpect(status().isOk());
		
		verify(agendamentoService).alterarStatusDoAgendamento(AGENDAMENTO_ID, statusRequest);
	}
	
	@Test
	@WithMockUser
	public void deveRetornar403QuandoUsuarioNaoForAdminAoAtualizarStatusDoAgendamento() throws JacksonException, Exception {
		AgendamentoStatusRequest statusRequest = new AgendamentoStatusRequest(AgendamentoStatus.CANCELADO);
		
		mockMvc.perform(patch(BASE_URL+"/id/status/admin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(statusRequest)))
		.andExpect(status().isForbidden());
		
		verify(agendamentoService, never()).alterarStatusDoAgendamento(AGENDAMENTO_ID, statusRequest);
	}
	
	@Test
	@WithMockUser
	public void deveRetornar200EOsDadosDoResponseDevemEstarCorretoAoProcurarAgendamentoPorId() throws JacksonException, Exception {
		when(agendamentoService.procurarAgendamentoPorId(AGENDAMENTO_ID)).thenReturn(agendamentoResponse);
		
		mockMvc.perform(get(BASE_URL+"/id"))
		.andExpect(status().isOk())
		.andExpect(content().string(objectMapper.writeValueAsString(agendamentoResponse)));
	}
	
	@Test
	@WithMockUser
	public void devRetornar404QuandoAgendamentoNaoForEncontradoAoProcurarAgendamentoPorId() throws JacksonException, Exception {
		doThrow(new NaoEncontradoException("Agendamento")).when(agendamentoService).procurarAgendamentoPorId(AGENDAMENTO_ID);
		
		mockMvc.perform(get(BASE_URL+"/id"))
		.andExpect(status().isNotFound());
	}
}
