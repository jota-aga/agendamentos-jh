package com.jh.auth_service.service;

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

import com.jh.auth_service.domain.User;
import com.jh.auth_service.domain.UserRole;
import com.jh.auth_service.dto.LoginRequest;
import com.jh.auth_service.dto.UserRequest;
import com.jh.auth_service.exceptions.EmailRepetidoExecption;
import com.jh.auth_service.exceptions.LoginIncorretoException;
import com.jh.auth_service.exceptions.NaoEncontradoException;
import com.jh.auth_service.repository.UserRepository;
import com.jh.auth_service.repository.UserRoleRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserService userService;
    
    private UserRole role;
    
	private UserRequest userRequest;
	
	private LoginRequest loginRequest;
	
	private User user;

    
    @BeforeEach
    public void setUp() {
    	role = new UserRole(1L, UserRole.Role.CLIENT.name());
    	userRequest = new UserRequest("joao@email.com", "João", "12345678");
    	loginRequest = new LoginRequest("joao@email.com", "12345678");
    	user = new User(1L, loginRequest.email(), userRequest.nome(), loginRequest.senha(), Set.of(role));
    }
    
    @Test
    public void deveSalvarNovoUsuario() {    	
    	when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.empty());
    	when(passwordEncoder.encode(userRequest.senha())).thenReturn("senha criptografada");
    	when(userRoleRepository.findByNome(role.getNome())).thenReturn(Optional.of(role));
    	
    	userService.salvarNovoUsuario(userRequest);
    	
    	verify(userRepository).save(any());
    }
    
    @Test
    public void deveLancarExceptionQuandoEmailERepetido() {
    	when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.of(new User()));
    	
    	assertThrows(EmailRepetidoExecption.class, () -> userService.salvarNovoUsuario(userRequest));
    	
    	verify(userRepository, never()).save(any());
    }
    
    @Test
    public void deveLancarExceptionQuandoRoleNaoEncontrada() {
    	when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.empty());
    	when(passwordEncoder.encode(userRequest.senha())).thenReturn("senha criptografada");
    	when(userRoleRepository.findByNome(role.getNome())).thenReturn(Optional.empty());

    	assertThrows(NaoEncontradoException.class, () -> userService.salvarNovoUsuario(userRequest));
    	
    	verify(userRepository, never()).save(any());
    }
    
    @Test
    public void deveRealizarLogin() {
    	when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
    	when(passwordEncoder.matches(loginRequest.senha(), user.getSenha())).thenReturn(true);
    	when(tokenService.gerarToken(user)).thenReturn("token");
    	
    	String token = userService.realizarLogin(loginRequest);
    	
    	assertFalse(token.isEmpty());
    }
    
    @Test
    public void deveLancarExceptionQuandoUsuarioNaoEncontrado() {
    	when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.empty());
    	
    	assertThrows(LoginIncorretoException.class, () -> userService.realizarLogin(loginRequest));
    }
    
    @Test
    public void deveLancarExceptionQuandoSenhasNaoSaoIguais() {
    	when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
    	when(passwordEncoder.matches(loginRequest.senha(), user.getSenha())).thenReturn(false);
    	
    	assertThrows(LoginIncorretoException.class, () -> userService.realizarLogin(loginRequest));
    }
}	
