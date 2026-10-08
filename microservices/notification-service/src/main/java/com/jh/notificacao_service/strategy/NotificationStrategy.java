package com.jh.notificacao_service.strategy;

import com.jh.notificacao_service.dto.AgendamentoCriadoEvent;

public interface NotificationStrategy {
	void enviarNotificacao(AgendamentoCriadoEvent agendamentoCriadoEvent);
}
