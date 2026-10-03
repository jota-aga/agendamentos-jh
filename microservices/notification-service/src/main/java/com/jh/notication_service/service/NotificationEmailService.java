package com.jh.notication_service.service;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.jh.notication_service.dto.AgendamentoCriadoEvent;
import com.jh.notication_service.dto.UsuarioResponse;
import com.jh.notication_service.strategy.NotificationMessageStrategy;
import com.jh.notication_service.strategy.NotificationStrategy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationEmailService implements NotificationMessageStrategy, NotificationStrategy {
	@Value("${notification.email.from}")
	private String emailFrom;

	private final JavaMailSender javaMailSender;
	
	private final UserExternalService userExternalService;
	
	@Override
	public void enviarNotificacao(AgendamentoCriadoEvent agendamentoCriadoEvent) {
		UsuarioResponse usuarioResponse = userExternalService.procurarUsuarioPorId(agendamentoCriadoEvent.usuarioId()); 
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		
		mailMessage.setFrom(emailFrom);
		mailMessage.setTo(usuarioResponse.email());
		mailMessage.setText(criarMensagemParaAgendamentoCriado(agendamentoCriadoEvent));
		mailMessage.setSubject("Agendamento na Barbearia JH");
		
		javaMailSender.send(mailMessage);
	}

	@Override
	public String criarMensagemParaAgendamentoCriado(AgendamentoCriadoEvent agendamentoCriadoEvent) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String dataFormatada = agendamentoCriadoEvent.data()
				.format(formatter);
		
		String mensagem = """
					Olá,

					Seu agendamento foi realizado com sucesso!

					Data: %s
					Horário: %s
					Procedimento: %s

				""".formatted(dataFormatada, agendamentoCriadoEvent.inicio(),
				agendamentoCriadoEvent.tituloDoProcedimento());

		return mensagem;
	}
}
