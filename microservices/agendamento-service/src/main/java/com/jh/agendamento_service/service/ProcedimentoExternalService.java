package com.jh.agendamento_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.jh.agendamento_service.dto.ProcedimentoResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProcedimentoExternalService {
	
	private final WebClient webClient;
	
	public ProcedimentoResponse procurarProcedimentoPorId(Long id) {
		return webClient.get()
		.uri("/procedimento/"+id)
		.retrieve()
		.onStatus(
	            status -> status.value() == 404,
	            response -> Mono.error(
	                new RuntimeException("Procedimento não encontrado")
	            )
	     )
		.bodyToMono(ProcedimentoResponse.class)
		.block();
	}
}
