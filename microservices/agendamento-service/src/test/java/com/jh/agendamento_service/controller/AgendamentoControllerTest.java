package com.jh.agendamento_service.controller;

import static org.mockito.ArgumentMatchers.any;
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
import com.jh.agendamento_service.enums.AgendamentoStatus;
import com.jh.agendamento_service.exception.ConflitoDeHorarioException;
import com.jh.agendamento_service.exception.ConflitoDeOperacaoException;
import com.jh.agendamento_service.exception.NaoAutorizadoException;
import com.jh.agendamento_service.exception.NaoEncontradoException;
import com.jh.agendamento_service.service.AgendamentoService;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = AgendamentoController.class)
@Import(SecurityConfig.class)
public class AgendamentoControllerTest {
	private static final String BASE_URL = "/agendamento";

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
	public void deveRetornar200AoAtualizarAgendamento() throws JacksonException, Exception {
		mockMvc.perform(put(BASE_URL + "/id").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest))).andExpect(status().isOk());

		verify(agendamentoService).atualizarAgendamento(AGENDAMENTO_ID, agendamentoRequest);
	}

	@Test
	@WithMockUser
	public void deveRetornar404QuandoAgendamentoNaoForEncontradoAoAtualizarAgendamento()
			throws JacksonException, Exception {
		NaoEncontradoException exception = new NaoEncontradoException("Agendamento");
		doThrow(exception).when(agendamentoService).atualizarAgendamento(AGENDAMENTO_ID, agendamentoRequest);

		mockMvc.perform(put(BASE_URL + "/id").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest))).andExpect(status().isNotFound())
				.andExpect(content().string(exception.getMessage()));

		verify(agendamentoService).atualizarAgendamento(AGENDAMENTO_ID, agendamentoRequest);

	}

	@Test
	@WithMockUser
	public void deveRetornar400QuandoRequestForInvalidoAoAtualizarAgendamento() throws JacksonException, Exception {
		mockMvc.perform(put(BASE_URL + "/id").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new AgendamentoRequest(null, null, null))))
				.andExpect(status().isBadRequest());

		verify(agendamentoService, never()).atualizarAgendamento(AGENDAMENTO_ID, agendamentoRequest);
	}

	@Test
	@WithMockUser
	public void deveRetornar404QuandoAlgumaProcuraNaoForNaoForEncontradaAoAtualizarAgendamento()
			throws JacksonException, Exception {
		NaoEncontradoException exception = new NaoEncontradoException("Objecto nao Encontrado");
		doThrow(exception).when(agendamentoService).atualizarAgendamento(AGENDAMENTO_ID, agendamentoRequest);

		mockMvc.perform(put(BASE_URL + "/id").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest))).andExpect(status().isNotFound())
				.andExpect(content().string(exception.getMessage()));
	}

	@Test
	@WithMockUser
	public void deveRetornar409QuandoAgendamentoResultarEmConflitoAoAtualizarAgendamento()
			throws JacksonException, Exception {
		ConflitoDeHorarioException exception = new ConflitoDeHorarioException();
		doThrow(exception).when(agendamentoService).atualizarAgendamento(AGENDAMENTO_ID, agendamentoRequest);

		mockMvc.perform(put(BASE_URL + "/id").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest))).andExpect(status().isConflict())
				.andExpect(content().string(exception.getMessage()));
	}

	@Test
	@WithMockUser
	public void deveRetornar200AoProcurarAgendamentoPorId() throws JacksonException, Exception {
		when(agendamentoService.procurarAgendamentoPorId(AGENDAMENTO_ID)).thenReturn(agendamentoResponse);

		mockMvc.perform(get(BASE_URL + "/id")).andExpect(status().isOk())
				.andExpect(content().string(objectMapper.writeValueAsString(agendamentoResponse)));
	}

	@Test
	@WithMockUser
	public void deveRetornar404QuandoAgendamentoNaoForEncontradoAoProcurarAgendamentoPorId()
			throws JacksonException, Exception {
		doThrow(new NaoEncontradoException("Agendamento")).when(agendamentoService)
				.procurarAgendamentoPorId(AGENDAMENTO_ID);

		mockMvc.perform(get(BASE_URL + "/id")).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser
	public void deveRetornar204AoCancelarAgendamentoComSucesso() throws Exception {
		mockMvc.perform(patch(BASE_URL + "/" + AGENDAMENTO_ID + "/cancelar")).andExpect(status().isNoContent());

		verify(agendamentoService).cancelarAgendamento(AGENDAMENTO_ID);
	}

	@Test
	@WithMockUser
	public void deveRetornar404QuandoAgendamentoNaoForEncontradoAoCancelarAgendamento() throws Exception {
		NaoEncontradoException ex = new NaoEncontradoException("Agendamento");
		doThrow(ex).when(agendamentoService).cancelarAgendamento(AGENDAMENTO_ID);

		mockMvc.perform(patch(BASE_URL + "/" + AGENDAMENTO_ID + "/cancelar")).andExpect(status().isNotFound())
				.andExpect(content().string(ex.getMessage()));

		verify(agendamentoService).cancelarAgendamento(AGENDAMENTO_ID);

	}

	@Test
	@WithMockUser
	public void deveRetornar409QuandoHouverConflitoDeOperacaoAoCancelarAgendamento() throws Exception {
		ConflitoDeOperacaoException ex = new ConflitoDeOperacaoException("Houve um conflito de operação");
		doThrow(ex).when(agendamentoService).cancelarAgendamento(AGENDAMENTO_ID);

		mockMvc.perform(patch(BASE_URL + "/" + AGENDAMENTO_ID + "/cancelar")).andExpect(status().isConflict())
				.andExpect(content().string(ex.getMessage()));

		verify(agendamentoService).cancelarAgendamento(AGENDAMENTO_ID);
	}

	@Test
	@WithMockUser
	public void deveRetornar403QuandoUsuarioNaoForDonoDoAgendamentoAoCancelarAgendamento() throws Exception {
		NaoAutorizadoException ex = new NaoAutorizadoException("Agendamento nao lhe pertence");
		doThrow(ex).when(agendamentoService).cancelarAgendamento(AGENDAMENTO_ID);

		mockMvc.perform(patch(BASE_URL + "/" + AGENDAMENTO_ID + "/cancelar")).andExpect(status().isUnauthorized())
				.andExpect(content().string(ex.getMessage()));

		verify(agendamentoService).cancelarAgendamento(AGENDAMENTO_ID);
	}

	@Test
	@WithMockUser
	public void deveRetornar200EOsAgendamentosDoUsuarioAutenticadoComSucesso() throws Exception {
		AgendamentoResponse agendamentoResponseCancelado = new AgendamentoResponse("id 2", 1L, "usuario", LocalDateTime.now(), data.minusMonths(1), horario,
				horario.plusMinutes(30), "procedimento", 30, BigDecimal.TEN, "categoria", AgendamentoStatus.CANCELADO);
		
		List<AgendamentoResponse> listaDeResponse = List.of(agendamentoResponse, agendamentoResponseCancelado);
		
		String responseEsperada = objectMapper.writeValueAsString(listaDeResponse);
		
		when(agendamentoService.listarAgendamentosDoUsuario()).thenReturn(listaDeResponse);
		
		mockMvc.perform(get(BASE_URL))
		.andExpect(status().isOk())
		.andExpect(content().string(responseEsperada));
		
		verify(agendamentoService).listarAgendamentosDoUsuario();
	}
	
	@Test
	@WithMockUser
	public void deveRetornar200EListarHorariosAoListarHorariosDisponiveis() throws Exception {
		LocalTime inicioDoExpediente = LocalTime.of(8, 0);
		LocalTime fimDoExpediente = LocalTime.of(18, 0);
		Long procedimentoId = 1L;
		LocalTime duasHoras = LocalTime.of(2, 0);
		LocalTime tresHoras = duasHoras.plusHours(1);
		
		List<LocalTime> listaDeResponse = List.of(duasHoras, tresHoras);
		
		String responseEsperada = objectMapper.writeValueAsString(listaDeResponse);
		
		when(agendamentoService.horariosDisponiveis(inicioDoExpediente, fimDoExpediente, data, procedimentoId)).thenReturn(listaDeResponse);
		
		mockMvc.perform(get(BASE_URL+"/horarios")
				.param("inicioDoExpediente", inicioDoExpediente.toString())
				.param("fimDoExepediente", fimDoExpediente.toString())
				.param("data", data.toString())
				.param("procedimentoId", procedimentoId.toString()))
		.andExpect(status().isOk())
		.andExpect(content().string(responseEsperada));
		
		verify(agendamentoService).horariosDisponiveis(inicioDoExpediente, fimDoExpediente, data, procedimentoId);
	}
}
