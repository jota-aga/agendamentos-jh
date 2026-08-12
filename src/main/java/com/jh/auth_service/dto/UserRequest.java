package com.jh.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
		
		@Email(message = "Email não é válido")
		@NotBlank(message = "Email não deve ser vazio")
		String email, 
		
		@NotBlank(message="Nome não deve ser vazio")
		String nome, 
		
		@NotBlank(message = "Senha não deve ser vazia")
		@Size(min = 8, message = "Senha deve conter mais de 7 caracteres")
		String senha
		) {

}
