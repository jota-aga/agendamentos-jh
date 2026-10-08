package com.jh.notificacao_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.jh.notificacao_service.dto.AutenticacaoServicoExternoDTO;
import com.jh.notificacao_service.dto.LoginResponse;
import com.jh.notificacao_service.dto.UsuarioResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserExternalService {
	
	@Value("${secret.api.notificacao.service}")
	private String notificacaoServiceSecret;

	private final WebClient webClient;

	public UsuarioResponse procurarUsuarioPorId(Long id) {
		
		LoginResponse loginResponse = getTokenDeAcesso();

		UsuarioResponse usuarioResponse = webClient.get().uri("/usuario/" + id)
				.headers(headers -> headers.setBearerAuth(loginResponse.token()))
				.retrieve()
				.bodyToMono(UsuarioResponse.class)
				.block();
		
		return usuarioResponse;
	}

	private LoginResponse getTokenDeAcesso() {
		AutenticacaoServicoExternoDTO servicoExterno = new AutenticacaoServicoExternoDTO("notificacao-service",
				notificacaoServiceSecret);
		
		LoginResponse loginResponse = webClient.post()
				.uri("/auth/login/servico")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(servicoExterno)
				.retrieve()
				.bodyToMono(LoginResponse.class)
				.block();
		
		return loginResponse;
	}
}
