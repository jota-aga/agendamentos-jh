package com.jh.auth_service.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.jh.auth_service.domain.User;
import com.jh.auth_service.dto.LoginRequest;
import com.jh.auth_service.dto.UserRequest;
import com.jh.auth_service.repository.UserRepository;
import com.jh.auth_service.service.UserService;

import jakarta.transaction.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerIntegrationTest {
	private final static String BASE_URL = "/auth";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	private UserRequest userRequest;
	
	private LoginRequest loginRequest;
	
	@BeforeEach
	public void setUp() {
		userRequest = new UserRequest("email@email", "nome", "senha123");
		loginRequest = new LoginRequest(userRequest.email(), userRequest.senha());
		userRepository.deleteAll();
	}
	
	@Test
	public void deveSalvarUsuarioComSucesso() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userRequest)))
		.andExpect(status().isCreated());
		
		List<User> users = userRepository.findAll();
		
		assertEquals(users.size(), 1);
	}
	
	@Test
	public void naoDeveSalvarUsuarioQuandoEmailJaExistir() throws JacksonException, Exception {
		userService.salvarNovoUsuario(userRequest);
		
		mockMvc.perform(post(BASE_URL+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userRequest)))
		.andExpect(status().isConflict());
		
		List<User> users = userRepository.findAll();
		
		assertEquals(users.size(), 1);
	}
	
	@Test
	public void deveRetornarTokenQuandoLoginCorreto() throws JacksonException, Exception {
		userService.salvarNovoUsuario(userRequest);
		
		mockMvc.perform(post(BASE_URL+"/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.token").exists());
		
	}
	
	@Test
	public void naoDeveRetornarTokenQuandoUsernameNaoEncontrado() throws JacksonException, Exception {
		loginRequest = new LoginRequest("emailincorreto@email.com", userRequest.senha());
		
		mockMvc.perform(post(BASE_URL+"/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
		.andExpect(status().isUnauthorized())
		.andExpect(jsonPath("$.token").doesNotExist());
		
	}
	
	@Test
	public void naoDeveRetornarTokenQuandoSenhaIncorreta() throws JacksonException, Exception {
		userService.salvarNovoUsuario(userRequest);
		loginRequest = new LoginRequest(userRequest.email(), "senhaincorreta");
		
		mockMvc.perform(post(BASE_URL+"/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
		.andExpect(status().isUnauthorized())
		.andExpect(jsonPath("$.token").doesNotExist());
		
	}
}
