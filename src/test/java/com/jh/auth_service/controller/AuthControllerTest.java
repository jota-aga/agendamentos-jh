package com.jh.auth_service.controller;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.jh.auth_service.configuration.SecurityConfig;
import com.jh.auth_service.dto.UserRequest;
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
	
	@BeforeEach
	public void setUp() {
		userRequest = new UserRequest("email@email.com", "nome", "senha123");
	}
	
	@Test
	public void deveRegistrarOUsuarioERetornar201() throws JacksonException, Exception {
		mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userRequest)))
		.andExpect(MockMvcResultMatchers.status().isCreated());
	
	}
}
