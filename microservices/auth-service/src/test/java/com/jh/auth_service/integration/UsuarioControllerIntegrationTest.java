package com.jh.auth_service.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.jh.auth_service.domain.Usuario;
import com.jh.auth_service.domain.UsuarioRole;
import com.jh.auth_service.dto.UsuarioResponse;
import com.jh.auth_service.repository.UsuarioRepository;
import com.jh.auth_service.repository.UsuarioRoleRepository;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class UsuarioControllerIntegrationTest {
	
	private final String BASE_URL = "/usuario";
	
	private final String SCOPE_ADMIN = "SCOPE_ADMIN";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private UsuarioRoleRepository roleRepository;
	
	private Usuario usuario;
	
	private UsuarioResponse usuarioResponse;
	
	@BeforeEach
	public void setUp() {
		usuarioRepository.deleteAll();
		
		UsuarioRole role = roleRepository.findByNome(UsuarioRole.Role.ADMIN.name()).get();
		
		usuario = new Usuario();
		usuario.setEmail("usuario@email.com");
		usuario.setNome("nome");
		usuario.setSenha("senha");
		usuario.setRoles(Set.of(role));
		
		usuario = usuarioRepository.save(usuario);
		
		usuarioResponse = new UsuarioResponse(usuario.getEmail(), usuario.getNome());

	}
	
	@Test
	@WithMockUser(authorities = SCOPE_ADMIN)
	public void deveProcurarUsuarioPorIdComSucesso() throws JacksonException, Exception {
		mockMvc.perform(get(BASE_URL+"/"+usuario.getId().toString()))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(usuarioResponse)));
	}
	
	@Test
	@WithMockUser(authorities = SCOPE_ADMIN)
	public void deveProcurarUsuarioPorEmailComSucesso() throws JacksonException, Exception {		
		mockMvc.perform(get(BASE_URL+"/email")
				.param("email", usuario.getEmail()))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(usuarioResponse)));
	}
	
	@Test
	@WithMockUser(authorities = SCOPE_ADMIN)
	public void deveProcurarUsuarioPorNomeComSucesso() throws JacksonException, Exception {		
		mockMvc.perform(get(BASE_URL+"/nome")
				.param("nome", usuario.getNome()))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(List.of(usuarioResponse))));
	}
}
