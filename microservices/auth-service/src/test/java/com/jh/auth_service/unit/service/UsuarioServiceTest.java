package com.jh.auth_service.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jh.auth_service.domain.Usuario;
import com.jh.auth_service.dto.UsuarioResponse;
import com.jh.auth_service.exceptions.NaoEncontradoException;
import com.jh.auth_service.repository.UsuarioRepository;
import com.jh.auth_service.service.UsuarioService;

@ExtendWith(MockitoExtension.class)

public class UsuarioServiceTest {
	@InjectMocks
	private UsuarioService usuarioService;
	
	@Mock
	private UsuarioRepository usuarioRepository;
	
	private Usuario usuario;
	
	private Long usuarioId;
	
	private String usuarioEmail;
	
	@BeforeEach
	public void setUp() {
		usuarioId = 1L;
		usuarioEmail = "usuario@email.com";
		
		usuario = new Usuario();
		usuario.setId(usuarioId);
		usuario.setEmail(usuarioEmail);
		usuario.setNome("nome");
	}
	
	@Test
	public void procurarUsuarioPorIdComSucesso() {
		when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
		
		UsuarioResponse usuarioResponse = usuarioService.procurarUsuarioPorId(usuarioId);
		
		assertEquals(usuario.getNome(), "nome");
		assertEquals(usuario.getEmail(), usuarioResponse.email());
	}
	
	@Test
	public void deveLancarNaoEncontradoExceptionQuandoUsuarioNaoForEncotradoAoProcurarPorId() {
		when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());
		
		assertThrows(NaoEncontradoException.class, () -> usuarioService.procurarUsuarioPorId(usuarioId));
	}
	
	@Test
	public void procurarUsuarioPorEmailComSucesso() {
		when(usuarioRepository.findByEmail(usuarioEmail)).thenReturn(Optional.of(usuario));
		
		UsuarioResponse usuarioResponse = usuarioService.procurarUsuarioPorEmail(usuarioEmail);
		
		assertEquals(usuario.getNome(), "nome");
		assertEquals(usuario.getEmail(), usuarioResponse.email());
	}
	
	@Test
	public void deveLancarNaoEncontradoExceptionQuandoUsuarioNaoForEncotradoAoProcurarPorEmail() {
		when(usuarioRepository.findByEmail(usuarioEmail)).thenReturn(Optional.empty());
		
		assertThrows(NaoEncontradoException.class, () -> usuarioService.procurarUsuarioPorEmail(usuarioEmail));
	}
}
