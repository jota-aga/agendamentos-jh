package com.jh.notificacao_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioResponse(
@Email(message = "Email não é válido")
@NotBlank(message = "Email não deve ser vazio")
String email, 

@NotBlank(message="Nome não deve ser vazio")
String nome) {

}
