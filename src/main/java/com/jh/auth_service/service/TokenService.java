package com.jh.auth_service.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.jh.auth_service.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {
	
	@Value("${token.seconds.expiration}")
	private Long secondsExpiration;
	
	@Value("${token.issuer}")
	private String issuer;
	
	private final JwtEncoder jwtEncoder;
	
	public String gerarToken(User user) {
		var scope = user.getRole()
				.getNome();
		
		String stringId = user.getId()
				.toString();
		
		Instant now = Instant.now();
		
		Instant expiresAt = now.plusSeconds(secondsExpiration);

		var claims = JwtClaimsSet.builder()
				.issuer(issuer)
				.subject(stringId)
				.issuedAt(now)
				.expiresAt(expiresAt)
				.claim("scope", scope)
				.build();

		var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

		return jwtValue;
	}
}
