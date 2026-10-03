package com.jh.notication_service.strategy;

import com.jh.notication_service.dto.AgendamentoCriadoEvent;

public interface NotificationMessageStrategy {
	String criarMensagemParaAgendamentoCriado(AgendamentoCriadoEvent agendamentoCriadoEvent);
}
