package com.jh.auth_service.unit.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
	
	private final String SCOPE_NOTIFICACAO_SERVICE = "SCOPE_NOTIFICACAO_SERVICE";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockitoBean
	private UsuarioService usuarioService;
	
	private String usuarioEmail;
	
	private Long usuarioId;
	
	private UsuarioResponse usuarioResponse;
	
	@BeforeEach
	public void setUp() {
		usuarioEmail = "usuario@email.com";
		usuarioId = 1L;
		
		usuarioResponse = new UsuarioResponse(usuarioEmail, "nome");
	}
	
	@Test
	@WithMockUser(authorities = SCOPE_ADMIN)
	public void deveProcurarUsuarioPorIdComSucessoQuandoUsuarioAutenticadoForAdmin() throws JacksonException, Exception {
		when(usuarioService.procurarUsuarioPorId(usuarioId)).thenReturn(usuarioResponse);
		
		mockMvc.perform(get(BASE_URL+"/"+usuarioId.toString()))
			.andExpect(status().isOk())
			.andExpect(content().string(objectMapper.writeValueAsString(usuarioResponse)));
	}
}
