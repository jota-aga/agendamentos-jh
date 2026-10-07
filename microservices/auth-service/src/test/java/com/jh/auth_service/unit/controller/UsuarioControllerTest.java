package com.jh.auth_service.unit.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.jh.auth_service.controller.UsuarioController;
import com.jh.auth_service.dto.UsuarioResponse;
import com.jh.auth_service.exceptions.NaoEncontradoException;
import com.jh.auth_service.infra.SecurityConfig;
import com.jh.auth_service.service.UsuarioService;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UsuarioController.class)
@Import(SecurityConfig.class)
public class UsuarioControllerTest {
	
	private final String BASE_URL = "/usuario";
	
	private final String SCOPE_ADMIN = "SCOPE_ADMIN";
	
	private final String SCOPE_NOTIFICACAO_SERVICE = "SCOPE_NOTIFICATION_SERVICE";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockitoBean
	private UsuarioService usuarioService;
	
	private String usuarioEmail;
	
	private String usuarioNome;
	
	private Long usuarioId;
	
	private UsuarioResponse usuarioResponse;
	
	@BeforeEach
	public void setUp() {
		usuarioNome = "nome";
		usuarioEmail = "usuario@email.com";
		usuarioId = 1L;
		
		usuarioResponse = new UsuarioResponse(usuarioEmail, usuarioNome);
	}
	
	@Test
	@WithMockUser(authorities = SCOPE_ADMIN)
	public void deveProcurarUsuarioPorIdComSucessoQuandoUsuarioAutenticadoForAdmin() throws JacksonException, Exception {
		when(usuarioService.procurarUsuarioPorId(usuarioId)).thenReturn(usuarioResponse);
		
		mockMvc.perform(get(BASE_URL+"/"+usuarioId.toString()))
			.andExpect(status().isOk())
			.andExpect(content().string(objectMapper.writeValueAsString(usuarioResponse)));
	}
	
	@Test
	@WithMockUser(authorities = SCOPE_NOTIFICACAO_SERVICE)
	public void deveProcurarUsuarioPorIdComSucessoQuandoUsuarioAutenticadoForNotificationService() throws JacksonException, Exception {
		when(usuarioService.procurarUsuarioPorId(usuarioId)).thenReturn(usuarioResponse);
		
		mockMvc.perform(get(BASE_URL+"/"+usuarioId.toString()))
			.andExpect(status().isOk())
			.andExpect(content().string(objectMapper.writeValueAsString(usuarioResponse)));
	}
	
	@Test
	@WithMockUser
	public void deveRetornar403QuandoUsuarioAutenticadoNaoTemPermissaoAoProcurarUsuarioPorId() throws JacksonException, Exception {
		mockMvc.perform(get(BASE_URL+"/"+usuarioId.toString()))
			.andExpect(status().isForbidden());
		
		verify(usuarioService, never()).procurarUsuarioPorId(usuarioId);
	}
	
	@Test
	@WithMockUser
	public void deveRetornar404QuandoUsuarioNaoForEncontradoAoProcurarUsuarioPorId() throws JacksonException, Exception {
		NaoEncontradoException exception = new NaoEncontradoException("usuario");
		doThrow(exception).when(usuarioService).procurarUsuarioPorId(usuarioId);
		
		mockMvc.perform(get(BASE_URL+"/"+usuarioId.toString()))
			.andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser(authorities = SCOPE_ADMIN)
	public void deveProcurarUsuarioPorEmailComSucessoQuandoUsuarioAutenticadoForAdmin() throws JacksonException, Exception {
		when(usuarioService.procurarUsuarioPorEmail(usuarioEmail)).thenReturn(usuarioResponse);
		
		mockMvc.perform(get(BASE_URL+"/email")
				.param("email", usuarioEmail))
			.andExpect(status().isOk())
			.andExpect(content().string(objectMapper.writeValueAsString(usuarioResponse)));
	}
	
	@Test
	@WithMockUser(authorities = SCOPE_NOTIFICACAO_SERVICE)
	public void deveProcurarUsuarioPorEmailComSucessoQuandoUsuarioAutenticadoForNotificacaoService() throws JacksonException, Exception {
		when(usuarioService.procurarUsuarioPorEmail(usuarioEmail)).thenReturn(usuarioResponse);
		
		mockMvc.perform(get(BASE_URL+"/email")
				.param("email", usuarioEmail))
			.andExpect(status().isOk())
			.andExpect(content().string(objectMapper.writeValueAsString(usuarioResponse)));
	}
	
	@Test
	@WithMockUser
	public void deveRetornar403QuandoUsuarioAutenticadoNaoTemPermissaoAoProcurarUsuarioPorEmail() throws JacksonException, Exception {
		mockMvc.perform(get(BASE_URL+"/email")
				.param("email", usuarioEmail))
			.andExpect(status().isForbidden());
		
		verify(usuarioService, never()).procurarUsuarioPorEmail(usuarioEmail);
	}
	
	@Test
	@WithMockUser
	public void deveRetornar404QuandoUsuarioNaoForEncontradoAoProcurarUsuarioPorEmail() throws JacksonException, Exception {
		NaoEncontradoException exception = new NaoEncontradoException("usuario");
		doThrow(exception).when(usuarioService).procurarUsuarioPorEmail(usuarioEmail);
		
		mockMvc.perform(get(BASE_URL+"/email")
				.param("email", usuarioEmail))
			.andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser(authorities = SCOPE_ADMIN)
	public void deveProcurarUsuarioPorNomeComSucessoQuandoUsuarioAutenticadoForAdmin() throws JacksonException, Exception {
		when(usuarioService.procurarUsuarioPorNome(usuarioNome)).thenReturn(List.of(usuarioResponse));
		
		mockMvc.perform(get(BASE_URL+"/nome")
				.param("nome", usuarioNome))
			.andExpect(status().isOk())
			.andExpect(content().string(objectMapper.writeValueAsString(List.of(usuarioResponse))));
	}
	
	@Test
	@WithMockUser(authorities = SCOPE_NOTIFICACAO_SERVICE)
	public void deveProcurarUsuarioPorNomeComSucessoQuandoUsuarioAutenticadoForNotificacaoService() throws JacksonException, Exception {
		when(usuarioService.procurarUsuarioPorNome(usuarioNome)).thenReturn(List.of(usuarioResponse));
		
		mockMvc.perform(get(BASE_URL+"/nome")
				.param("nome", usuarioNome))
			.andExpect(status().isOk())
			.andExpect(content().string(objectMapper.writeValueAsString(List.of(usuarioResponse))));
	}
}
