package com.jh.agendamento_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.jh.agendamento_service.dto.ProcedimentoResponse;
import com.jh.agendamento_service.exception.NaoEncontradoException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProcedimentoExternalService {
	
	@Value("${procedimento.service.url}")
	private String PROCEDIMENTO_SERVICE_URL;
	
	private final WebClient webClient;
	
	public ProcedimentoResponse procurarProcedimentoPorId(Long id) {
		return webClient.get()
		.uri(PROCEDIMENTO_SERVICE_URL+"/procedimento/"+id)
		.retrieve()
		.onStatus(
	            status -> status.value() == 404,
	            response -> Mono.error(
	                new NaoEncontradoException("Procedimento")
	            )
	     )
		.bodyToMono(ProcedimentoResponse.class)
		.block();
	}
}
