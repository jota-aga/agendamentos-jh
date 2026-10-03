package com.jh.auth_service.service;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.jh.auth_service.domain.Usuario;
import com.jh.auth_service.domain.UsuarioRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {
	
	@Value("${token.seconds.expiration}")
	private Long secondsExpiration;
	
	@Value("${token.issuer}")
	private String issuer;
	
	private final JwtEncoder jwtEncoder;
	
	public String gerarToken(Usuario usuario) {
		var scope = usuario.getRoles()
				.stream()
				.map(UsuarioRole::getNome)
				.collect(Collectors.joining(" "));
		
		String stringId = usuario.getId()
				.toString();
		
		String nomeDoUsuario = usuario.getNome();
		
		Instant now = Instant.now();
		
		Instant expiresAt = now.plusSeconds(secondsExpiration);

		var claims = JwtClaimsSet.builder()
				.issuer(issuer)
				.subject(stringId)
				.issuedAt(now)
				.expiresAt(expiresAt)
				.claim("scope", scope)
				.claim("nome", nomeDoUsuario)
				.build();

		var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

		return jwtValue;
	}
}
