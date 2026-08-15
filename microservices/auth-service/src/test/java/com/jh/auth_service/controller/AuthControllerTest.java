package com.jh.auth_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.jh.auth_service.configuration.SecurityConfig;
import com.jh.auth_service.dto.LoginRequest;
import com.jh.auth_service.dto.UserRequest;
import com.jh.auth_service.exceptions.EmailRepetidoExecption;
import com.jh.auth_service.exceptions.LoginIncorretoException;
import com.jh.auth_service.service.UserService;

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
	private UserService userService;
	
	private UserRequest userRequest;
	
	private LoginRequest loginRequest;
	
	@BeforeEach
	public void setUp() {
		userRequest = new UserRequest("email@email.com", "nome", "senha123");
		loginRequest = new LoginRequest("email@email.com", "senha123");
	}
	
	@Test
	public void deveRegistrarOUsuarioERetornar201() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userRequest)))
		.andExpect(status().isCreated());
	
	}
	
	@Test
	public void deveRetornar400QuandoBodyIncorreto() throws JacksonException, Exception {
		userRequest = new UserRequest("emailmail.com", "", "");
		
		mockMvc.perform(post(BASE_URL+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userRequest)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.senha").value("Senha deve ter entre 8 a 32 caracteres"))
		.andExpect(jsonPath("$.nome").value("Nome não deve ser vazio"))
		.andExpect(jsonPath("$.email").value("Email não é válido"));
		
		verify(userService, never()).salvarNovoUsuario(any());
	}
	
	@Test
	public void deveRetornar409QuandoEmailRepetido() throws JacksonException, Exception {
		EmailRepetidoExecption ex = new EmailRepetidoExecption();
		doThrow(new EmailRepetidoExecption())
			.when(userService)
			.salvarNovoUsuario(userRequest);
		
		mockMvc.perform(post(BASE_URL+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userRequest)))
		.andExpect(status().isConflict())
		.andExpect(jsonPath("$").value(ex.getMessage()));
	}
	
	@Test
	public void deveRealizarLoginERetornar200() throws JacksonException, Exception {
		String token = "123456789";
		when(userService.realizarLogin(loginRequest)).thenReturn(token);
		
		mockMvc.perform(post(BASE_URL+"/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.token").value(token));
		
		verify(userService, atLeastOnce()).realizarLogin(loginRequest);
	}
	
	@Test
	public void deveRetonar401QuandoLoginIncorreto() throws JacksonException, Exception {
		LoginIncorretoException ex = new LoginIncorretoException();
		doThrow(new LoginIncorretoException())
		.when(userService)
		.realizarLogin(loginRequest);
		
		mockMvc.perform(post(BASE_URL+"/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
		.andExpect(status().isUnauthorized())
		.andExpect(jsonPath("$").value(ex.getMessage()));
	}
}
