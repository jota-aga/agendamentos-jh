package com.jh.procedimento_service.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.jh.procedimento_service.configuration.SecurityConfig;
import com.jh.procedimento_service.dto.procedimento.categoria.CategoriaRequest;
import com.jh.procedimento_service.exceptions.CategoriaRepetidaException;
import com.jh.procedimento_service.exceptions.NaoEncontradoException;
import com.jh.procedimento_service.service.CategoriaService;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CategoriaController.class)
@Import(SecurityConfig.class)
public class CategoriaControllerTest {

	private static final String BASE_URL = "/categoria";
	
	private static final Long CATEGORIA_ID = 1L;
	
	private static final String ADMIN = "SCOPE_ADMIN";
	
	private static final String CLIENT = "SCOPE_CLIENT";


	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private CategoriaService categoriaService;

	private CategoriaRequest categoriaRequest;


	@BeforeEach
	public void setUp() {
		categoriaRequest = new CategoriaRequest("nome");
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveCriarCategoriaERetornar201QuandoUsuarioForAdmin() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoriaRequest))).andExpect(status().isCreated());

		verify(categoriaService).criarCategoria(categoriaRequest);
	}

	@Test
	@WithMockUser(authorities = CLIENT)
	public void deveRetornar403QuandoUsuarioNaoForAdminAoCriarCategoria() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoriaRequest))).andExpect(status().isForbidden());

		verify(categoriaService, never()).criarCategoria(categoriaRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar400QuandoDTOForInvalidoAoCriarCategoria() throws JacksonException, Exception {
		categoriaRequest = new CategoriaRequest("");

		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoriaRequest))).andExpect(status().isBadRequest());

		verify(categoriaService, never()).criarCategoria(categoriaRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar409QuandoNomeJaExistirAoCriarCategoria() throws JacksonException, Exception {
		doThrow(new CategoriaRepetidaException()).when(categoriaService).criarCategoria(categoriaRequest);

		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoriaRequest))).andExpect(status().isConflict());
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar200QuandoUsuarioForAdminAoAtualizarCategoria() throws JacksonException, Exception {
		mockMvc.perform(put(BASE_URL + "/" + CATEGORIA_ID).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoriaRequest))).andExpect(status().isOk());

		verify(categoriaService).atualizarCategoria(CATEGORIA_ID, categoriaRequest);
	}

	@Test
	@WithMockUser(authorities = CLIENT)
	public void deveRetornar403QuandoUsuarioNaoForAdminAoAtualizarCategoria() throws JacksonException, Exception {
		mockMvc.perform(put(BASE_URL + "/" + CATEGORIA_ID).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoriaRequest))).andExpect(status().isForbidden());

		verify(categoriaService, never()).atualizarCategoria(CATEGORIA_ID, categoriaRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar400QuandoDTOForInvalidoAoAtualizarCategoria() throws JacksonException, Exception {
		categoriaRequest = new CategoriaRequest("");

		mockMvc.perform(put(BASE_URL + "/" + CATEGORIA_ID).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoriaRequest))).andExpect(status().isBadRequest());

		verify(categoriaService, never()).atualizarCategoria(CATEGORIA_ID, categoriaRequest);
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar409QuandoNomeJaExistirAoAtualizarCategoria() throws JacksonException, Exception {
		doThrow(new CategoriaRepetidaException()).when(categoriaService).atualizarCategoria(CATEGORIA_ID,
				categoriaRequest);

		mockMvc.perform(put(BASE_URL + "/" + CATEGORIA_ID).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoriaRequest))).andExpect(status().isConflict());
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar404QuandoCategoriaNaoForEncontradaAoAtualizarCategoria()
			throws JacksonException, Exception {
		doThrow(new NaoEncontradoException("Categoria Por Id")).when(categoriaService)
				.atualizarCategoria(CATEGORIA_ID, categoriaRequest);

		mockMvc.perform(put(BASE_URL + "/" + CATEGORIA_ID).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoriaRequest))).andExpect(status().isNotFound());
	}

	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveAlterarAtivoERetornar200QuandoUsuarioForAdmin() throws JacksonException, Exception {
		Boolean ativo = true;

		mockMvc.perform(patch(BASE_URL + "/ativo/" + CATEGORIA_ID).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ativo))).andExpect(status().isOk());

		verify(categoriaService).atualizarAtivo(CATEGORIA_ID, ativo);
	}

	@Test
	@WithMockUser(authorities = CLIENT)
	public void deveRetornar403QuandoUsuarioNaoForAdminAoAlterarAtivo() throws JacksonException, Exception {
		Boolean ativo = true;
		
		mockMvc.perform(patch(BASE_URL+"/ativo/"+CATEGORIA_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ativo)))
		.andExpect(status().isForbidden());
		
		verify(categoriaService, never()).atualizarAtivo(CATEGORIA_ID, ativo);
	}
	
	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar404QuandoCategoriaNaoForEncontradaAoAlterarAtivo()
			throws JacksonException, Exception {
		Boolean ativo = true;
		doThrow(new NaoEncontradoException("Categoria Por Id")).when(categoriaService)
				.atualizarAtivo(CATEGORIA_ID, ativo);
		
		mockMvc.perform(patch(BASE_URL+"/ativo/"+CATEGORIA_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ativo)))
		.andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveDeletarCategoriaERetornar200QuandoUsuarioForAdmin() throws JacksonException, Exception {

		mockMvc.perform(delete(BASE_URL + "/" + CATEGORIA_ID)).andExpect(status().isOk());

		verify(categoriaService).deletarCategoria(CATEGORIA_ID);
	}
	
	@Test
	@WithMockUser(authorities = CLIENT)
	public void deveRetornar403QuandoUsuarioNaoForAdmin() throws JacksonException, Exception {

		mockMvc.perform(delete(BASE_URL + "/" + CATEGORIA_ID)).andExpect(status().isOk());

		verify(categoriaService).deletarCategoria(CATEGORIA_ID);
	}
	
	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar404QuandoCategoriaNaoForEncontradaAoDeletarCategoria()
			throws JacksonException, Exception {
		
		doThrow(new NaoEncontradoException("Categoria Por Id")).when(categoriaService)
				.deletarCategoria(CATEGORIA_ID);
		
		mockMvc.perform(delete(BASE_URL+"/"+CATEGORIA_ID))
		.andExpect(status().isNotFound());
	}
	
	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveListarTodasCateogoriasERetonar200QuandoUsuarioForAdmin() throws JacksonException, Exception {

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk());

		verify(categoriaService).listarTodasCategorias();
	}
	
	@Test
	@WithMockUser(authorities = CLIENT)
	public void deveRetornar403QuandoUsuarioNaoForAdminAoListarTodasAsCategorias()
			throws JacksonException, Exception {
		
		mockMvc.perform(get(BASE_URL))
		.andExpect(status().isForbidden());
	}
}
