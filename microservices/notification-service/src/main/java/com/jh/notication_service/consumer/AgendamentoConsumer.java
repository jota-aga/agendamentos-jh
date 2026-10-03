package com.jh.notication_service.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jh.notication_service.dto.AgendamentoCriadoEvent;
import com.jh.notication_service.service.NotificationEmailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AgendamentoConsumer {
	
	private final NotificationEmailService notificationEmailService;
	
	@RabbitListener(queues = "notifications.email-send")
	public void receiveAgendamentoCriado(@Payload AgendamentoCriadoEvent agendamentoCriadoEvent) throws JsonMappingException, JsonProcessingException {
		System.out.println("Mensagem Recebida: "+agendamentoCriadoEvent);
		
		notificationEmailService.enviarNotificacao(agendamentoCriadoEvent);
	}
}
