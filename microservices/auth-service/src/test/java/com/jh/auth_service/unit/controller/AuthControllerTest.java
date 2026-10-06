package com.jh.auth_service.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.jh.auth_service.controller.AuthController;
import com.jh.auth_service.dto.AutenticacaoServicoExternoDTO;
import com.jh.auth_service.dto.LoginRequest;
import com.jh.auth_service.dto.LoginResponse;
import com.jh.auth_service.dto.UsuarioRequest;
import com.jh.auth_service.exceptions.EmailRepetidoExecption;
import com.jh.auth_service.exceptions.LoginIncorretoException;
import com.jh.auth_service.infra.SecurityConfig;
import com.jh.auth_service.service.AuthService;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {
	
	private static String BASE_URL = "/auth";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockitoBean
	private AuthService authService;
	
	private UsuarioRequest usuarioRequest;
	
	private LoginRequest loginRequest;
	
	private AutenticacaoServicoExternoDTO servicoExternoDTO;
	
	@BeforeEach
	public void setUp() {
		usuarioRequest = new UsuarioRequest("email@email.com", "nome", "senha123");
		loginRequest = new LoginRequest("email@email.com", "senha123");
		servicoExternoDTO = new AutenticacaoServicoExternoDTO("servico-id", "service-secret");
	}
	
	@Test
	public void deveRegistrarUsuarioERetornar201() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(usuarioRequest)))
		.andExpect(status().isCreated());
	
	}
	
	@Test
	public void deveRetornar400QuandoBodyIncorretoAoRegistrarUsuario() throws JacksonException, Exception {
		usuarioRequest = new UsuarioRequest("emailmail.com", "", "");
		
		mockMvc.perform(post(BASE_URL+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(usuarioRequest)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.senha").value("Senha deve ter entre 8 a 32 caracteres"))
		.andExpect(jsonPath("$.nome").value("Nome não deve ser vazio"))
		.andExpect(jsonPath("$.email").value("Email não é válido"));
		
		verify(authService, never()).registrarUsuario(any());
	}
	
	@Test
	public void deveRetornar409QuandoEmailForRepetidoAoRegistrarUsuario() throws JacksonException, Exception {
		EmailRepetidoExecption ex = new EmailRepetidoExecption();
		doThrow(new EmailRepetidoExecption())
			.when(authService)
			.registrarUsuario(usuarioRequest);
		
		mockMvc.perform(post(BASE_URL+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(usuarioRequest)))
		.andExpect(status().isConflict())
		.andExpect(jsonPath("$").value(ex.getMessage()));
	}
	
	@Test
	public void deveRealizarLoginDeUsuarioERetornar200() throws JacksonException, Exception {
		LoginResponse loginResponse = new LoginResponse("token");
		when(authService.realizarLoginDeUsuario(loginRequest)).thenReturn(loginResponse);
		
		mockMvc.perform(post(BASE_URL+"/login/usuario")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
		.andExpect(status().isOk())
		.andExpect(content().string(objectMapper.writeValueAsString(loginResponse)));
		
		verify(authService, atLeastOnce()).realizarLoginDeUsuario(loginRequest);
	}
	
	@Test
	public void deveRetonar401QuandoLoginDeUsuarioIncorreto() throws JacksonException, Exception {
		LoginIncorretoException ex = new LoginIncorretoException();
		doThrow(new LoginIncorretoException())
		.when(authService)
		.realizarLoginDeUsuario(loginRequest);
		
		mockMvc.perform(post(BASE_URL+"/login/usuario")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
		.andExpect(status().isUnauthorized())
		.andExpect(jsonPath("$").value(ex.getMessage()));
	}
	
	@Test
	public void deveRetornar200AoRealizarLoginDeServicoComSucesso() throws JacksonException, Exception {
		LoginResponse loginResponse = new LoginResponse("token");
		when(authService.realizarLoginDeServicoExterno(servicoExternoDTO)).thenReturn(loginResponse);
		
		mockMvc.perform(post(BASE_URL+"/login/servico")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(servicoExternoDTO)))
		.andExpect(status().isOk())
		.andExpect(content().json(objectMapper.writeValueAsString(loginResponse)));
	}
	
	@Test
	public void deveRetornar401QuandoLoginForIncorretoAoRealizarLoginDeServico() throws JacksonException, Exception {
		LoginIncorretoException exception = new LoginIncorretoException();
		doThrow(exception).when(authService).realizarLoginDeServicoExterno(servicoExternoDTO);
		
		mockMvc.perform(post(BASE_URL+"/login/servico")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(servicoExternoDTO)))
		.andExpect(status().isUnauthorized())
		.andExpect(content().string(exception.getMessage()));
	}
}
