package com.jh.agendamento_service.unit.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.jh.agendamento_service.controller.AgendamentoAdminController;
import com.jh.agendamento_service.dto.AgendamentoAdminRequest;
import com.jh.agendamento_service.dto.AgendamentoResponse;
import com.jh.agendamento_service.dto.AgendamentoStatusRequest;
import com.jh.agendamento_service.enums.AgendamentoStatus;
import com.jh.agendamento_service.exception.ConflitoDeHorarioException;
import com.jh.agendamento_service.exception.NaoEncontradoException;
import com.jh.agendamento_service.infra.SecurityConfig;
import com.jh.agendamento_service.service.AgendamentoAdminService;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AgendamentoAdminController.class)
@Import(SecurityConfig.class)
public class AgendamentoAdminControllerTest {

	private static final String BASE_URL = "/agendamento/admin";

	private static final String AGENDAMENTO_ID = "id";

	private static final String ADMIN = "SCOPE_ADMIN";

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

	private AgendamentoStatusRequest agendamentoStatusRequest;

	@BeforeEach
	public void setUp() {
		data = LocalDate.of(2026, 8, 18);
		horario = LocalTime.of(14, 0);
		agendamentoAdminRequest = new AgendamentoAdminRequest(1L, "usuario", data, horario, AgendamentoStatus.CONCLUIDO,
				1L);
		agendamentoResponse = new AgendamentoResponse(AGENDAMENTO_ID, 1L, "usuario", LocalDateTime.now(), data, horario,
				horario.plusMinutes(30), "procedimento", 30, BigDecimal.TEN, "categoria", AgendamentoStatus.AGENDADO);
		agendamentoStatusRequest = new AgendamentoStatusRequest(AgendamentoStatus.CANCELADO);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar200AoCriarAgendamentoComSucesso() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoAdminRequest))).andExpect(status().isOk());

		verify(agendamentoAdminService).criarAgendamento(agendamentoAdminRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar400QuandoRequestForIncorretoAoCriarAgendamento() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(new AgendamentoAdminRequest(null, null, null, null, null, null))))
				.andExpect(status().isBadRequest());

		verify(agendamentoAdminService, never()).criarAgendamento(agendamentoAdminRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar409QuandoAgendamentoResultarEmConflitoDeHorarioAoCriarAgendamento()
			throws JacksonException, Exception {
		ConflitoDeHorarioException ex = new ConflitoDeHorarioException();
		doThrow(ex).when(agendamentoAdminService).criarAgendamento(agendamentoAdminRequest);

		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoAdminRequest))).andExpect(status().isConflict())
				.andExpect(content().string(ex.getMessage()));

		verify(agendamentoAdminService).criarAgendamento(agendamentoAdminRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar404QuandoProcedimentoNaoForEncontradoAoCriarAgendamento()
			throws JacksonException, Exception {
		NaoEncontradoException ex = new NaoEncontradoException("procedimento");
		doThrow(ex).when(agendamentoAdminService).criarAgendamento(agendamentoAdminRequest);

		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoAdminRequest))).andExpect(status().isNotFound())
				.andExpect(content().string(ex.getMessage()));

		verify(agendamentoAdminService).criarAgendamento(agendamentoAdminRequest);
	}

	@Test
	@WithMockUser
	public void deveRetornar401QuandoUsuarioAutenticadoNaoForAdminAoCriarAgendamento()
			throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoAdminRequest))).andExpect(status().isForbidden());

		verify(agendamentoAdminService, never()).criarAgendamento(agendamentoAdminRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar200EOAgendamentoAoGetAgendamentoPorIdComSucesso() throws JacksonException, Exception {
		when(agendamentoAdminService.getAgendamentoPorId(AGENDAMENTO_ID)).thenReturn(agendamentoResponse);

		mockMvc.perform(get(BASE_URL + "/" + AGENDAMENTO_ID)).andExpect(status().isOk())
				.andExpect(content().string(objectMapper.writeValueAsString(agendamentoResponse)));

		verify(agendamentoAdminService).getAgendamentoPorId(AGENDAMENTO_ID);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar404QuandoAgendamentoNaoForEncontradoAoGetAgendamentoPorId()
			throws JacksonException, Exception {
		NaoEncontradoException ex = new NaoEncontradoException("procedimento");
		doThrow(ex).when(agendamentoAdminService).getAgendamentoPorId(AGENDAMENTO_ID);

		mockMvc.perform(get(BASE_URL + "/" + AGENDAMENTO_ID)).andExpect(status().isNotFound())
				.andExpect(content().string(ex.getMessage()));

		verify(agendamentoAdminService).getAgendamentoPorId(AGENDAMENTO_ID);
	}

	@Test
	@WithMockUser
	public void deveRetornar403QuandoUsuarioNaoForAdminAoProcurarAgendamentoPorId() throws JacksonException, Exception {
		mockMvc.perform(get(BASE_URL + "/" + AGENDAMENTO_ID)).andExpect(status().isForbidden());

		verify(agendamentoAdminService, never()).getAgendamentoPorId(AGENDAMENTO_ID);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar200AoAtualizarAgendamentoComSucesso() throws JacksonException, Exception {
		mockMvc.perform(put(BASE_URL + "/" + AGENDAMENTO_ID).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoAdminRequest))).andExpect(status().isOk());

		verify(agendamentoAdminService).atualizarAgendamento(AGENDAMENTO_ID, agendamentoAdminRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar400QuandoRequestForIncorretoAoAtualizarAgendamento() throws JacksonException, Exception {
		mockMvc.perform(put(BASE_URL + "/" + AGENDAMENTO_ID).contentType(MediaType.APPLICATION_JSON).content(
				objectMapper.writeValueAsString(new AgendamentoAdminRequest(null, null, null, null, null, null))))
				.andExpect(status().isBadRequest());

		verify(agendamentoAdminService, never()).atualizarAgendamento(AGENDAMENTO_ID, agendamentoAdminRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar409QuandoAgendamentoResultarEmConflitoDeHorarioAoAtualizarAgendamento()
			throws JacksonException, Exception {
		ConflitoDeHorarioException ex = new ConflitoDeHorarioException();
		doThrow(ex).when(agendamentoAdminService).atualizarAgendamento(AGENDAMENTO_ID, agendamentoAdminRequest);

		mockMvc.perform(put(BASE_URL + "/" + AGENDAMENTO_ID).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoAdminRequest))).andExpect(status().isConflict())
				.andExpect(content().string(ex.getMessage()));

		verify(agendamentoAdminService).atualizarAgendamento(AGENDAMENTO_ID, agendamentoAdminRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar404QuandoProcedimentoNaoForEncontradoAoAtualizarAgendamento()
			throws JacksonException, Exception {
		NaoEncontradoException ex = new NaoEncontradoException("procedimento");
		doThrow(ex).when(agendamentoAdminService).atualizarAgendamento(AGENDAMENTO_ID, agendamentoAdminRequest);

		mockMvc.perform(put(BASE_URL + "/" + AGENDAMENTO_ID).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoAdminRequest))).andExpect(status().isNotFound())
				.andExpect(content().string(ex.getMessage()));

		verify(agendamentoAdminService).atualizarAgendamento(AGENDAMENTO_ID, agendamentoAdminRequest);
	}

	@Test
	@WithMockUser
	public void deveRetornar401QuandoUsuarioAutenticadoNaoForAdminAoAtualizarAgendamento()
			throws JacksonException, Exception {
		mockMvc.perform(put(BASE_URL + "/" + AGENDAMENTO_ID).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoAdminRequest))).andExpect(status().isForbidden());

		verify(agendamentoAdminService, never()).atualizarAgendamento(AGENDAMENTO_ID, agendamentoAdminRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar200AoAlterarStatusDoAgendamentoComSucesso() throws JacksonException, Exception {
		mockMvc.perform(patch(BASE_URL + "/" + AGENDAMENTO_ID + "/status").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoStatusRequest))).andExpect(status().isOk());

		verify(agendamentoAdminService).alterarStatusDoAgendamento(AGENDAMENTO_ID, agendamentoStatusRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar400QuandoRequestForIncorretoAoAlterarStatusDoAgendamento()
			throws JacksonException, Exception {
		mockMvc.perform(patch(BASE_URL + "/" + AGENDAMENTO_ID + "/status").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new AgendamentoStatusRequest(null))))
				.andExpect(status().isBadRequest());

		verify(agendamentoAdminService, never()).alterarStatusDoAgendamento(AGENDAMENTO_ID, agendamentoStatusRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar404QuandoProcedimentoNaoForEncontradoAoAlterarStatusDoAgendamento()
			throws JacksonException, Exception {
		NaoEncontradoException ex = new NaoEncontradoException("procedimento");
		doThrow(ex).when(agendamentoAdminService).alterarStatusDoAgendamento(AGENDAMENTO_ID, agendamentoStatusRequest);

		mockMvc.perform(patch(BASE_URL + "/" + AGENDAMENTO_ID + "/status").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoStatusRequest))).andExpect(status().isNotFound())
				.andExpect(content().string(ex.getMessage()));

		verify(agendamentoAdminService).alterarStatusDoAgendamento(AGENDAMENTO_ID, agendamentoStatusRequest);
	}

	@Test
	@WithMockUser
	public void deveRetornar401QuandoUsuarioAutenticadoNaoForAdminAoAlterarStatusDoAgendamento()
			throws JacksonException, Exception {
		mockMvc.perform(patch(BASE_URL + "/" + AGENDAMENTO_ID + "/status").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoAdminRequest))).andExpect(status().isForbidden());

		verify(agendamentoAdminService, never()).alterarStatusDoAgendamento(AGENDAMENTO_ID, agendamentoStatusRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar200EOsAgendamentoFiltradosComSucesso() throws JacksonException, Exception {
		List<AgendamentoResponse> response = List.of(agendamentoResponse);
		
		when(agendamentoAdminService.listarAgendamentoFiltrados(1L, data, horario, horario.plusHours(6), "procedimento",
				AgendamentoStatus.CONCLUIDO, "data")).thenReturn(response);
		
		mockMvc.perform(get(BASE_URL)
				.param("usuarioId", "1")
				.param("data", data.toString())
				.param("inicioDoExpediente", horario.toString())
				.param("fimDoExpediente", horario.plusHours(6).toString())
				.param("tituloDoProcedimento", "procedimento")
				.param("status", AgendamentoStatus.CONCLUIDO.toString())
				.param("sortBy", "data"))
		.andExpect(status().isOk());	
		
		verify(agendamentoAdminService).listarAgendamentoFiltrados(1L, data, horario, horario.plusHours(6), "procedimento",
				AgendamentoStatus.CONCLUIDO, "data");
	}	
}
