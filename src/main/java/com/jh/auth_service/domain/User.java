package com.jh.auth_service.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Email(message = "Email não é válido")
	@NotBlank(message = "Email não deve ser vazio")
	private String email;
	
	@NotBlank(message="Nome não deve ser vazio")
	private String nome;
	
	@NotBlank(message = "Senha não deve ser vazia")
	@Size(min = 8, message = "Senha deve conter mais de 7 caracteres")
	private String senha;
	
	@ManyToOne
	@NotNull(message = "Role é obrigatório")
	private UserRole role;
}
