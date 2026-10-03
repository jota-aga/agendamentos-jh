package com.jh.notication_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.jh.notication_service.dto.UsuarioResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserExternalService {
	private final WebClient webClient;
	
	public UsuarioResponse procurarUsuarioPorId(Long id) {
		
		UsuarioResponse usuarioResponse = webClient.get()
			.uri("/usuario/"+id)
			.retrieve()
			.bodyToMono(UsuarioResponse.class)
			.block();
		
		return usuarioResponse;
	}
}
