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

import com.jh.auth_service.domain.Usuario;
import com.jh.auth_service.domain.UsuarioRole;
import com.jh.auth_service.dto.LoginRequest;
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

    
    @BeforeEach
    public void setUp() {
    	role = new UsuarioRole(1L, UsuarioRole.Role.CLIENT.name());
    	usuarioRequest = new UsuarioRequest("joao@email.com", "João", "12345678");
    	loginRequest = new LoginRequest("joao@email.com", "12345678");
    	usuario = new Usuario(1L, loginRequest.email(), usuarioRequest.nome(), loginRequest.senha(), Set.of(role));
    }
    
    @Test
    public void deveSalvarNovoUsuario() {    	
    	when(usuarioRepository.findByEmail(usuarioRequest.email())).thenReturn(Optional.empty());
    	when(passwordEncoder.encode(usuarioRequest.senha())).thenReturn("senha criptografada");
    	when(usuarioRoleRepository.findByNome(role.getNome())).thenReturn(Optional.of(role));
    	
    	authService.salvarNovoUsuario(usuarioRequest);
    	
    	verify(usuarioRepository).save(any());
    }
    
    @Test
    public void deveLancarExceptionQuandoEmailERepetido() {
    	when(usuarioRepository.findByEmail(usuarioRequest.email())).thenReturn(Optional.of(new Usuario()));
    	
    	assertThrows(EmailRepetidoExecption.class, () -> authService.salvarNovoUsuario(usuarioRequest));
    	
    	verify(usuarioRepository, never()).save(any());
    }
    
    @Test
    public void deveLancarExceptionQuandoRoleNaoEncontrada() {
    	when(usuarioRepository.findByEmail(usuarioRequest.email())).thenReturn(Optional.empty());
    	when(passwordEncoder.encode(usuarioRequest.senha())).thenReturn("senha criptografada");
    	when(usuarioRoleRepository.findByNome(role.getNome())).thenReturn(Optional.empty());

    	assertThrows(NaoEncontradoException.class, () -> authService.salvarNovoUsuario(usuarioRequest));
    	
    	verify(usuarioRepository, never()).save(any());
    }
    
    @Test
    public void deveRealizarLogin() {
    	when(usuarioRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(usuario));
    	when(passwordEncoder.matches(loginRequest.senha(), usuario.getSenha())).thenReturn(true);
    	when(tokenService.gerarToken(usuario)).thenReturn("token");
    	
    	String token = authService.realizarLogin(loginRequest);
    	
    	assertFalse(token.isEmpty());
    }
    
    @Test
    public void deveLancarExceptionQuandoUsuarioNaoEncontrado() {
    	when(usuarioRepository.findByEmail(loginRequest.email())).thenReturn(Optional.empty());
    	
    	assertThrows(LoginIncorretoException.class, () -> authService.realizarLogin(loginRequest));
    }
    
    @Test
    public void deveLancarExceptionQuandoSenhasNaoSaoIguais() {
    	when(usuarioRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(usuario));
    	when(passwordEncoder.matches(loginRequest.senha(), usuario.getSenha())).thenReturn(false);
    	
    	assertThrows(LoginIncorretoException.class, () -> authService.realizarLogin(loginRequest));
    }
}	
