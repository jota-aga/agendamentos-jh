package com.jh.agendamento_service.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jh.agendamento_service.dto.AgendamentoCriadoEvent;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AgendamentoEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${agendamento.exchange}")
    private String exchange;

    @Value("${agendamento.routing.key}")
    private String routingKey;
    
    private Logger log = LoggerFactory.getLogger(AgendamentoEventPublisher.class);

    public void publicarAgendamentoCriado(AgendamentoCriadoEvent agendamento) {
    	log.info("mensagem sendo publicada");
        rabbitTemplate.convertAndSend(
                exchange,
                routingKey,
                agendamento
        );
    }
}