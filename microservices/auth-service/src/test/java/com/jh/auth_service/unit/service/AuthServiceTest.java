package com.jh.auth_service.unit.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.jh.auth_service.domain.Usuario;
import com.jh.auth_service.domain.UsuarioRole;
import com.jh.auth_service.dto.AutenticacaoServicoExternoDTO;
import com.jh.auth_service.dto.LoginRequest;
import com.jh.auth_service.dto.LoginResponse;
import com.jh.auth_service.dto.UsuarioRequest;
import com.jh.auth_service.exceptions.EmailRepetidoExecption;
import com.jh.auth_service.exceptions.LoginIncorretoException;
import com.jh.auth_service.exceptions.NaoEncontradoException;
import com.jh.auth_service.repository.UsuarioRepository;
import com.jh.auth_service.repository.UsuarioRoleRepository;
import com.jh.auth_service.service.AuthService;
import com.jh.auth_service.service.TokenService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private UsuarioRoleRepository usuarioRoleRepository;

	@Mock
	private BCryptPasswordEncoder passwordEncoder;

	@Mock
	private TokenService tokenService;

	@InjectMocks
	private AuthService authService;

	private UsuarioRole role;

	private UsuarioRequest usuarioRequest;

	private LoginRequest loginRequest;

	private Usuario usuario;

	private AutenticacaoServicoExternoDTO servicoExternoDTO;

	@BeforeEach
	public void setUp() {
		role = new UsuarioRole(1L, UsuarioRole.Role.CLIENT.name());
		usuarioRequest = new UsuarioRequest("joao@email.com", "João", "12345678");
		loginRequest = new LoginRequest("joao@email.com", "12345678");
		usuario = new Usuario(1L, loginRequest.email(), usuarioRequest.nome(), loginRequest.senha(), Set.of(role));
		servicoExternoDTO = new AutenticacaoServicoExternoDTO("servico-id", "service-secret");
		ReflectionTestUtils.setField(authService, "secretApiNotificao", "service-secret");
	}

	@Test
	public void deveRegistrarUsuarioComSucesso() {
		when(usuarioRepository.findByEmail(usuarioRequest.email())).thenReturn(Optional.empty());
		when(passwordEncoder.encode(usuarioRequest.senha())).thenReturn("senha criptografada");
		when(usuarioRoleRepository.findByNome(role.getNome())).thenReturn(Optional.of(role));

		authService.registrarUsuario(usuarioRequest);

		verify(usuarioRepository).save(any());
	}

	@Test
	public void deveLancarExceptionQuandoEmailERepetidoAoRegistrarUsuario() {
		when(usuarioRepository.findByEmail(usuarioRequest.email())).thenReturn(Optional.of(new Usuario()));

		assertThrows(EmailRepetidoExecption.class, () -> authService.registrarUsuario(usuarioRequest));

		verify(usuarioRepository, never()).save(any());
	}

	@Test
	public void deveLancarExceptionQuandoRoleNaoEncontradaAoRegistrarUsuario() {
		when(usuarioRepository.findByEmail(usuarioRequest.email())).thenReturn(Optional.empty());
		when(passwordEncoder.encode(usuarioRequest.senha())).thenReturn("senha criptografada");
		when(usuarioRoleRepository.findByNome(role.getNome())).thenReturn(Optional.empty());

		assertThrows(NaoEncontradoException.class, () -> authService.registrarUsuario(usuarioRequest));

		verify(usuarioRepository, never()).save(any());
	}

	@Test
	public void deveRealizarLoginDeUsuarioComSucesso() {
		when(usuarioRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(usuario));
		when(passwordEncoder.matches(loginRequest.senha(), usuario.getSenha())).thenReturn(true);
		when(tokenService.gerarTokenParaUsuario(usuario)).thenReturn("token");

		LoginResponse loginResponse = authService.realizarLoginDeUsuario(loginRequest);

		assertFalse(loginResponse.token().isEmpty());
	}

	@Test
	public void deveLancarLoginIncorretoExceptionQuandoUsuarioNaoEncontradoAoRealizarLoginDeUsuario() {
		when(usuarioRepository.findByEmail(loginRequest.email())).thenReturn(Optional.empty());

		assertThrows(LoginIncorretoException.class, () -> authService.realizarLoginDeUsuario(loginRequest));
	}

	@Test
	public void deveLancarLoginIncorretoExceptionQuandoSenhasNaoSaoIguaisAoRealizarLoginDeUsuario() {
		when(usuarioRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(usuario));
		when(passwordEncoder.matches(loginRequest.senha(), usuario.getSenha())).thenReturn(false);

		assertThrows(LoginIncorretoException.class, () -> authService.realizarLoginDeUsuario(loginRequest));
	}

	@Test
	public void deveRealizarLoginDeServicoComSucesso() {
		when(tokenService.gerarTokenParaServicoExterno(servicoExternoDTO)).thenReturn("token");

		LoginResponse loginResponse = authService.realizarLoginDeServicoExterno(servicoExternoDTO);

		assertFalse(loginResponse.token().isEmpty());
	}
	
	@Test
	public void deveLancarLoginIncorretoQuandoSecretForIncorretoAoRealizarLoginDeServico() {
		servicoExternoDTO = new AutenticacaoServicoExternoDTO("servico-id", "secret-incorreta");
		
		assertThrows(LoginIncorretoException.class, () -> authService.realizarLoginDeServicoExterno(servicoExternoDTO));
	}
}
