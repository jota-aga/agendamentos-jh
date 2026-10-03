package com.jh.auth_service.service;

import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jh.auth_service.domain.Usuario;
import com.jh.auth_service.domain.UsuarioRole;
import com.jh.auth_service.dto.LoginRequest;
import com.jh.auth_service.dto.UsuarioRequest;
import com.jh.auth_service.exceptions.EmailRepetidoExecption;
import com.jh.auth_service.exceptions.LoginIncorretoException;
import com.jh.auth_service.exceptions.NaoEncontradoException;
import com.jh.auth_service.repository.UsuarioRepository;
import com.jh.auth_service.repository.UsuarioRoleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UsuarioRepository usuarioRepository;

	private final UsuarioRoleRepository usuarioRoleRepository;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	private final TokenService tokenService;
	
	@Transactional
	public void salvarNovoUsuario(UsuarioRequest usuarioRequest) {
		validarNovoUsuario(usuarioRequest);

		Usuario usuario = criarUsuario(usuarioRequest);

		usuarioRepository.save(usuario);
	}

	public String realizarLogin(LoginRequest loginRequest) {
		Usuario usuario = usuarioRepository.findByEmail(loginRequest.email())
				.orElseThrow(() -> new LoginIncorretoException());
		
		if (!bCryptPasswordEncoder.matches(loginRequest.senha(),usuario.getSenha())) {
			throw new LoginIncorretoException();
		}
		
		return tokenService.gerarToken(usuario);
	}

	private void validarNovoUsuario(UsuarioRequest usuarioRequest) {
		if (usuarioRepository.findByEmail(usuarioRequest.email()).isPresent())
			throw new EmailRepetidoExecption();
	}

	private Usuario criarUsuario(UsuarioRequest usuarioRequest) {
		Usuario usuario = new Usuario();
		usuario.setEmail(usuarioRequest.email());
		usuario.setNome(usuarioRequest.nome());

		String senhaCriptografada = bCryptPasswordEncoder.encode(usuarioRequest.senha());
		usuario.setSenha(senhaCriptografada);
		
		UsuarioRole role = usuarioRoleRepository.findByNome(UsuarioRole.Role.CLIENT.name())
				.orElseThrow(() -> new NaoEncontradoException("Role"));

		usuario.setRoles(Set.of(role));

		return usuario;
	}
}
