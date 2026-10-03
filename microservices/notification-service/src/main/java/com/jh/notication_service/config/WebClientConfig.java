package com.jh.notication_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	
	@Value("${auth.service.base.url}")
	private String authServiceBaseUrl;
	
	@Bean
	public WebClient authServiceWebClient() {
		return WebClient.builder()
				.baseUrl(authServiceBaseUrl)
				.build();
	}
}
