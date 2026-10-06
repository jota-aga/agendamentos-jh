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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import com.jh.auth_service.domain.Usuario;
import com.jh.auth_service.dto.LoginRequest;
import com.jh.auth_service.dto.UsuarioRequest;
import com.jh.auth_service.repository.UsuarioRepository;
import com.jh.auth_service.service.AuthService;

import jakarta.transaction.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Transactional
public class AuthControllerIntegrationTest {
	
	@Container
	private MySQLContainer container = new MySQLContainer("mysql:8.4");
	
	private final static String BASE_URL = "/auth";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private AuthService authService;
	
	private UsuarioRequest usuarioRequest;
	
	private LoginRequest loginRequest;
	
	@BeforeEach
	public void setUp() {
		usuarioRequest = new UsuarioRequest("email@email", "nome", "senha123");
		loginRequest = new LoginRequest(usuarioRequest.email(), usuarioRequest.senha());
		usuarioRepository.deleteAll();
	}
	
	@Test
	public void deveRegistrarUsuarioComSucesso() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(usuarioRequest)))
		.andExpect(status().isCreated());
		
		List<Usuario> usuarios = usuarioRepository.findAll();
		
		assertEquals(usuarios.size(), 1);
	}
	
	@Test
	public void naoDeveRegistrarUsuarioQuandoEmailJaExistir() throws JacksonException, Exception {
		authService.registrarUsuario(usuarioRequest);
		
		mockMvc.perform(post(BASE_URL+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(usuarioRequest)))
		.andExpect(status().isConflict());
		
		List<Usuario> usuarios = usuarioRepository.findAll();
		
		assertEquals(usuarios.size(), 1);
	}
	
	@Test
	public void deveRetornar200ETokenAoRealizarLoginComSucesso() throws JacksonException, Exception {
		authService.registrarUsuario(usuarioRequest);
		
		mockMvc.perform(post(BASE_URL+"/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.token").exists());
		
	}
	
	@Test
	public void deveRetornarQuandoUsuarioNameEmailNaoForEncontradoAoRealizarLoginDeUsuario() throws JacksonException, Exception {
		loginRequest = new LoginRequest("emailincorreto@email.com", usuarioRequest.senha());
		
		mockMvc.perform(post(BASE_URL+"/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
		.andExpect(status().isUnauthorized())
		.andExpect(jsonPath("$.token").doesNotExist());
		
	}
	
	@Test
	public void deveRetornar401QuandoASenhaForIncorretaAoRealizarLoginDoUsuario() throws JacksonException, Exception {
		authService.registrarUsuario(usuarioRequest);
		loginRequest = new LoginRequest(usuarioRequest.email(), "senhaincorreta");
		
		mockMvc.perform(post(BASE_URL+"/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)))
		.andExpect(status().isUnauthorized())
		.andExpect(jsonPath("$.token").doesNotExist());
		
	}
}
