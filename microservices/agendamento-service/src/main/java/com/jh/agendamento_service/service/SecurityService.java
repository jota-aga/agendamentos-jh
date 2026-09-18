package com.jh.agendamento_service.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.jh.agendamento_service.dto.UsuarioAutenticado;

@Service
public class SecurityService {
	
	public UsuarioAutenticado getUsuarioAutenticado() {
		Authentication authentication = SecurityContextHolder.getContext()
			.getAuthentication();
		
		Jwt jwt = (Jwt) authentication.getPrincipal();
		
		Long usuarioId = Long.valueOf(jwt.getSubject());
		String nomeDoUsuario = jwt.getClaimAsString("nome");
		
		UsuarioAutenticado usuarioAutenticado = new UsuarioAutenticado(usuarioId, nomeDoUsuario);
		return usuarioAutenticado;
	}
}
