package com.jh.auth_service.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.jh.auth_service.domain.User;
import com.jh.auth_service.domain.UserRole;
import com.jh.auth_service.dto.LoginRequest;
import com.jh.auth_service.dto.UserRequest;
import com.jh.auth_service.exceptions.EmailRepetidoExecption;
import com.jh.auth_service.exceptions.LoginIncorretoException;
import com.jh.auth_service.exceptions.NaoEncotradoException;
import com.jh.auth_service.repository.UserRepository;
import com.jh.auth_service.repository.UserRoleRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private final UserRoleRepository userRoleRepository;

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	private final JwtEncoder jwtEncoder;

	public void salvarNovoUsuario(UserRequest userRequest) {
		validarNovoUser(userRequest);

		User user = criarUsuario(userRequest);

		userRepository.save(user);
	}

	public String realizarLogin(LoginRequest loginRequest) {
		User user = userRepository.findByEmail(loginRequest.email())
				.orElseThrow(() -> new LoginIncorretoException());
		
		if (bCryptPasswordEncoder.matches(loginRequest.senha(),
				user.getSenha()) == false) {
			throw new LoginIncorretoException();
		}
		
		return gerarToken(user);
	}

	private void validarNovoUser(UserRequest userRequest) {
		if (userRepository.findByEmail(userRequest.email()).isPresent())
			throw new EmailRepetidoExecption();
	}

	private User criarUsuario(UserRequest userRequest) {
		User user = new User();
		user.setEmail(userRequest.email());
		user.setNome(userRequest.nome());

		String senhaCriptografada = bCryptPasswordEncoder.encode(userRequest.senha());
		user.setSenha(senhaCriptografada);

		UserRole role = userRoleRepository.findByNome(UserRole.Role.CLIENT.name())
				.orElseThrow(() -> new NaoEncotradoException("Role"));

		user.setRole(role);

		return user;
	}

	private String gerarToken(User user) {
		var scope = user.getRole();
		Instant expiresAt = LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));

		var claims = JwtClaimsSet.builder().issuer("mybackend").subject(user.getId().toString()).issuedAt(Instant.now())
				.expiresAt(expiresAt).claim("scope", scope).build();

		var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

		return jwtValue;
	}
}
