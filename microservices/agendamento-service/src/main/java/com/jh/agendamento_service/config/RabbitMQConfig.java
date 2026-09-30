package com.jh.agendamento_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
	
	@Value("${notification.queue}")
	private String NOTIFICATION_QUEUE;
	
	@Value("${agendamento.exchange}")
	private String AGENDAMENTO_EXCHANGE;
	
	@Value("${agendamento.routing.key}")
	private String AGENDAMENTO_ROUTING_KEY;
	
	@Bean
	public MessageConverter jsonMessageConverter() {
		return new JacksonJsonMessageConverter();
	}
	
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(jsonMessageConverter());
		
		return template;
	}
	
	@Bean
	public Queue notificationQueue() {
		return new Queue(NOTIFICATION_QUEUE);
	}
	
	@Bean
	public Exchange notificationExchange() {
		return new DirectExchange(AGENDAMENTO_EXCHANGE);
	}
	
	@Bean
	public Binding notificationBinding() {
		return BindingBuilder.bind(notificationQueue())
				.to(notificationExchange())
				.with(AGENDAMENTO_ROUTING_KEY)
				.noargs();

	}
}
