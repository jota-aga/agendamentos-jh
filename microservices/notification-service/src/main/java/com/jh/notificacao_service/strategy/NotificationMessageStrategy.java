package com.jh.notificacao_service.strategy;

import com.jh.notificacao_service.dto.AgendamentoCriadoEvent;

public interface NotificationMessageStrategy {
	String criarMensagemParaAgendamentoCriado(AgendamentoCriadoEvent agendamentoCriadoEvent);
}
