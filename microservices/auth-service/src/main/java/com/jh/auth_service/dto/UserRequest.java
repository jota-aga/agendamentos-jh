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
		
		@Size(min = 8,max = 32, message = "Senha deve ter entre 8 a 32 caracteres")
		String senha
		) {

}
