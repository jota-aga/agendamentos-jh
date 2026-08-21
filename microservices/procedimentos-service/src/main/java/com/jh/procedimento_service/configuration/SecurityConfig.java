package com.jh.procedimento_service.configuration;

import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	@Value("${jwt.public.key}")
	private RSAPublicKey publicKey;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
				.authorizeHttpRequests(authorize -> authorize
															 .requestMatchers(HttpMethod.POST, "/categoria").hasAuthority("SCOPE_ADMIN")
															 .requestMatchers(HttpMethod.PUT, "/categoria/**").hasAuthority("SCOPE_ADMIN")
															 .requestMatchers(HttpMethod.PATCH, "/categoria/**").hasAuthority("SCOPE_ADMIN")
															 .requestMatchers(HttpMethod.GET, "/categoria/ativos").permitAll()
															 .requestMatchers(HttpMethod.GET, "/categoria").hasAuthority("SCOPE_ADMIN")
															 .requestMatchers(HttpMethod.POST, "/procedimento").hasAuthority("SCOPE_ADMIN")
															 .requestMatchers(HttpMethod.PUT, "/procedimento").hasAuthority("SCOPE_ADMIN")
															 .requestMatchers(HttpMethod.PATCH, "/procedimento").hasAuthority("SCOPE_ADMIN")
															 .requestMatchers(HttpMethod.GET, "/procedimento/ativos").permitAll()
															 .requestMatchers(HttpMethod.GET, "/procedimento").hasAuthority("SCOPE_ADMIN")
															 .anyRequest().authenticated()
															 )
															 
				.csrf(csrf -> csrf.disable())
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.build();
	}
	
	@Bean
	public JwtDecoder jwtDecoder()  {
		return NimbusJwtDecoder.withPublicKey(publicKey).build();
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}