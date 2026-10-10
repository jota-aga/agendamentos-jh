package com.jh.auth_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jh.auth_service.dto.AutenticacaoServicoExternoDTO;
import com.jh.auth_service.dto.LoginRequest;
import com.jh.auth_service.dto.LoginResponse;
import com.jh.auth_service.dto.UsuarioRequest;
import com.jh.auth_service.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Tag(
	    name = "Autenticação",
	    description = "Registro e Autenticação dos usuários."
	)
public class AuthController {
	
	private final AuthService authService;
	
	@PostMapping("/register")
	@Operation(summary = "Registro de usuário", description ="Registra um usuário do tipo CLIENT no sistema e o email deve ser único.")
	@ApiResponse(responseCode = "201", description = "Usuario registrado com sucesso.", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class)) })
	@ApiResponse(responseCode = "409", description = "Email já cadastrado no sistema.", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
	public ResponseEntity<?> registrarUser(@Valid @RequestBody UsuarioRequest usuarioRequest){
		authService.registrarUsuario(usuarioRequest);
		
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PostMapping("/login/usuario")
	@Operation(summary = "Login de usuário", description = "O usuário deve ter feito o registro para conseguir realizar o login")
	@ApiResponse(responseCode = "200", description = "Retorna token.", content = { @Content(mediaType = "application/json", 
	          schema = @Schema(implementation = LoginResponse.class)) })
	@ApiResponse(responseCode = "401", description = "Login incorreto.", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
	public ResponseEntity<?> realizarLoginParaUsuario(@RequestBody LoginRequest loginRequest){
		LoginResponse loginResponse = authService.realizarLoginDeUsuario(loginRequest);
		
		return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
	}
	
	@PostMapping("/login/servico")
	@Operation(summary = "Login de serviço externo" , description = "O serviço externo deve ter acesso a API para conseguir ter o retorno do token")
	@ApiResponse(responseCode = "200", description = "Login correto e retorna token.", content = { @Content(mediaType = "application/json", 
	          schema = @Schema(implementation = LoginResponse.class)) })
	@ApiResponse(responseCode = "401", description = "Login incorreto.")
	public ResponseEntity<?> realizarLoginParaServicoExterno(@RequestBody  AutenticacaoServicoExternoDTO externoDTO){
		LoginResponse loginResponse = authService.realizarLoginDeServicoExterno(externoDTO);
		
		return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
	}
}
