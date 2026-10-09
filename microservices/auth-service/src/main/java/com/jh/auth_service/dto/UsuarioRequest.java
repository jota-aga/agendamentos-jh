package com.jh.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
		
		@Schema(
		        description = "Email para usar no login (deve ser único do sistema).",
		        example = "joao@email.com"
		    )
		@Email(message = "Email não é válido")
		@NotBlank(message = "Email não deve ser vazio")
		String email, 
		
		@Schema(
		        description = "Nome do usuário.",
		        example = "João Henrique"
		    )
		@NotBlank(message="Nome não deve ser vazio")
		String nome, 
		
		@Schema(
		        description = "Senha para usar no login.",
		        example = "joao1234"
		    )
		@Size(min = 8,max = 32, message = "Senha deve ter entre 8 a 32 caracteres")
		String senha
		) {

}
