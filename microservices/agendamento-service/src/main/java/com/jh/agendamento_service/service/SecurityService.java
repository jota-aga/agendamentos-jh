package com.jh.agendamento_service.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.jh.agendamento_service.dto.UsuarioAutenticadoDTO;

@Service
public class SecurityService {
	
	public UsuarioAutenticadoDTO getUsuarioAutenticado() {
		Authentication authentication = SecurityContextHolder.getContext()
			.getAuthentication();
		
		Jwt jwt = (Jwt) authentication.getPrincipal();
		
		Long usuarioId = Long.valueOf(jwt.getSubject());
		String nomeDoUsuario = jwt.getClaimAsString("nome");
		
		UsuarioAutenticadoDTO usuarioAutenticado = new UsuarioAutenticadoDTO(usuarioId, nomeDoUsuario);
		return usuarioAutenticado;
	}
}
