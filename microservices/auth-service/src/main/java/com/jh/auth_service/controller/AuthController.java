package com.jh.auth_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jh.auth_service.dto.AutenticacaoServicoExternoDTO;
import com.jh.auth_service.dto.LoginRequest;
import com.jh.auth_service.dto.LoginResponse;
import com.jh.auth_service.dto.UsuarioRequest;
import com.jh.auth_service.service.AuthService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
	
	private final AuthService authService;
	
	@PostMapping("/register")
	public ResponseEntity<?> registrarUser(@Valid @RequestBody UsuarioRequest usuarioRequest){
		authService.salvarNovoUsuario(usuarioRequest);
		
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PostMapping("/login/usuario")
	public ResponseEntity<?> realizarLoginParaUsuario(@RequestBody LoginRequest loginRequest){
		String token = authService.realizarLoginDeUsuario(loginRequest);
		LoginResponse loginResponse = new LoginResponse(token);
		
		return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
	}
	
	@PostMapping("/login/servico")
	public ResponseEntity<?> realizarLoginParaServicoExterno(@RequestBody  AutenticacaoServicoExternoDTO externoDTO){
		String token = authService.realizarLoginDeServicoExterno(externoDTO);
		LoginResponse loginResponse = new LoginResponse(token);
		
		return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
	}
}
