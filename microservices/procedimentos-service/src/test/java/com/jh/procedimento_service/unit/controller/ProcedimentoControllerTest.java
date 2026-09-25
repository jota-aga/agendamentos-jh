package com.jh.procedimento_service.unit.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.jh.procedimento_service.controller.ProcedimentoController;
import com.jh.procedimento_service.dto.categoria.CategoriaResponse;
import com.jh.procedimento_service.dto.procedimento.ProcedimentoRequest;
import com.jh.procedimento_service.dto.procedimento.ProcedimentoResponse;
import com.jh.procedimento_service.exceptions.NaoEncontradoException;
import com.jh.procedimento_service.infra.SecurityConfig;
import com.jh.procedimento_service.service.ProcedimentoService;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProcedimentoController.class)
@Import(SecurityConfig.class)
public class ProcedimentoControllerTest {

	private final static String BASE_URL = "/procedimento";

	private final static String ADMIN = "SCOPE_ADMIN";

	private final static String CLIENT = "SCOPE_CLIENT";

	private final static Boolean ATIVO = false;

	private final static Long PROCEDIMENTO_ID = 1L;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ProcedimentoService procedimentoService;

	private ProcedimentoRequest procedimentoRequest;

	private List<ProcedimentoResponse> response;

	@BeforeEach
	public void setUp() {
		procedimentoRequest = new ProcedimentoRequest("titulo", "descricao", BigDecimal.ONE, 30, 1L);
		response = criarListaDeResponse();
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveCriarProcedimentoQuandoUsuarioForAdmin() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(procedimentoRequest))).andExpect(status().isCreated());

		verify(procedimentoService).criarProcedimento(procedimentoRequest);
	}

	@Test
	@WithMockUser(authorities = CLIENT)
	public void deveRetornar403QuandoUsuarioNaoForAdminAoCriarProcedimento() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(procedimentoRequest))).andExpect(status().isForbidden());

		verify(procedimentoService, never()).criarProcedimento(procedimentoRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar201QuandoCategoriaNaoForEncontradoAoCriarProcedimento() throws JacksonException, Exception {
		doThrow(new NaoEncontradoException("Categoria por Id")).when(procedimentoService)
				.criarProcedimento(procedimentoRequest);

		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(procedimentoRequest))).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar400QuandoRequestForInvalidoAoCriarProcedimento() throws JacksonException, Exception {
		procedimentoRequest = new ProcedimentoRequest(null, null, null, null, null);

		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(procedimentoRequest))).andExpect(status().isBadRequest());

		verify(procedimentoService, never()).criarProcedimento(procedimentoRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveAtualizarProcedimentoQuandoUsuarioForAdmin() throws JacksonException, Exception {
		mockMvc.perform(put(BASE_URL + "/" + PROCEDIMENTO_ID).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(procedimentoRequest))).andExpect(status().isOk());

		verify(procedimentoService).atualizarProcedimento(PROCEDIMENTO_ID, procedimentoRequest);
	}

	@Test
	@WithMockUser(authorities = CLIENT)
	public void deveRetornar403QuandoUsuarioNaoForAdminAoAtualizarProcedimento() throws JacksonException, Exception {
		mockMvc.perform(put(BASE_URL + "/" + PROCEDIMENTO_ID).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(procedimentoRequest))).andExpect(status().isForbidden());

		verify(procedimentoService, never()).atualizarProcedimento(PROCEDIMENTO_ID, procedimentoRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar201QuandoCategoriaOuProcedimentoNaoForEncontradoAoAtualizarProcedimento()
			throws JacksonException, Exception {
		doThrow(new NaoEncontradoException("")).when(procedimentoService).atualizarProcedimento(PROCEDIMENTO_ID,
				procedimentoRequest);

		mockMvc.perform(put(BASE_URL + "/" + PROCEDIMENTO_ID).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(procedimentoRequest))).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar400QuandoRequestForInvalidoAoAtualizarProcedimento() throws JacksonException, Exception {
		procedimentoRequest = new ProcedimentoRequest(null, null, null, null, null);

		mockMvc.perform(put(BASE_URL + "/" + PROCEDIMENTO_ID).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(procedimentoRequest))).andExpect(status().isBadRequest());

		verify(procedimentoService, never()).atualizarProcedimento(PROCEDIMENTO_ID, procedimentoRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveAlterarAtivoDoProcedimentoQuandoUsuarioForAdmin() throws JacksonException, Exception {

		mockMvc.perform(patch(BASE_URL + "/ativo/" + PROCEDIMENTO_ID).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ATIVO))).andExpect(status().isOk());

		verify(procedimentoService).alterarAtivo(PROCEDIMENTO_ID, ATIVO);
	}

	@Test
	@WithMockUser(authorities = CLIENT)
	public void deveRetornar403QuandoUsuarioNaoForAdminAoAlterarAtivo() throws JacksonException, Exception {
		mockMvc.perform(patch(BASE_URL + "/ativo/" + PROCEDIMENTO_ID).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ATIVO))).andExpect(status().isForbidden());

		verify(procedimentoService, never()).alterarAtivo(PROCEDIMENTO_ID, ATIVO);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar201QuandoProcedimentoNaoForEncontradoAoAlterarAtivo() throws JacksonException, Exception {
		doThrow(new NaoEncontradoException("Procedimento por Id")).when(procedimentoService)
				.alterarAtivo(PROCEDIMENTO_ID, ATIVO);

		mockMvc.perform(patch(BASE_URL + "/ativo/" + PROCEDIMENTO_ID).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ATIVO))).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveListarTodosOsProcedimentosQuandoUsuarioForAdmin() throws JacksonException, Exception {
		when(procedimentoService.procurarTodosProcedimentos()).thenReturn(response);

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(response)));

		verify(procedimentoService).procurarTodosProcedimentos();
	}

	@Test
	@WithMockUser(authorities = CLIENT)
	public void deveRetornar403QuandoUsuarioForClientAoListarTodosOsProcedimentos() throws JacksonException, Exception {
		mockMvc.perform(get(BASE_URL)).andExpect(status().isForbidden());

		verify(procedimentoService, never()).procurarTodosProcedimentos();
	}

	@Test
	public void listarProcedimentosAtivos() throws JacksonException, Exception {
		when(procedimentoService.procurarProcedimentosAtivos()).thenReturn(response);

		mockMvc.perform(get(BASE_URL + "/ativos")).andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(response)));

		verify(procedimentoService).procurarProcedimentosAtivos();
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveDeletarERetornar200QuandoUsuarioForAdmin() throws JacksonException, Exception {
		mockMvc.perform(delete(BASE_URL + "/" + PROCEDIMENTO_ID)).andExpect(status().isOk());

		verify(procedimentoService).deletarProcedimento(PROCEDIMENTO_ID);
	}

	@Test
	@WithMockUser(authorities = CLIENT)
	public void deveRetornar403QuandoUsuarioNaoForAdminAoDeletarProcedimento() throws JacksonException, Exception {
		mockMvc.perform(delete(BASE_URL + "/" + PROCEDIMENTO_ID)).andExpect(status().isForbidden());

		verify(procedimentoService, never()).deletarProcedimento(PROCEDIMENTO_ID);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar201QuandoProcedimentoNaoForEncontradoAoDeletarProcedimento()
			throws JacksonException, Exception {
		doThrow(new NaoEncontradoException("Procedimento por Id")).when(procedimentoService)
				.deletarProcedimento(PROCEDIMENTO_ID);

		mockMvc.perform(delete(BASE_URL + "/" + PROCEDIMENTO_ID)).andExpect(status().isNotFound());
	}

	private List<ProcedimentoResponse> criarListaDeResponse() {
		CategoriaResponse categoriaResponse = new CategoriaResponse(1L, "categoria", true);

		ProcedimentoResponse procedimentoResponse = new ProcedimentoResponse(1L, procedimentoRequest.titulo(), procedimentoRequest.descricao(),
				procedimentoRequest.preco(), procedimentoRequest.duracaoEmMinutos(), ATIVO, categoriaResponse);
		
		return List.of(procedimentoResponse);
	}
}
