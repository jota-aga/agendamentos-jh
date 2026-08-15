package com.jh.auth_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jh.auth_service.dto.LoginRequest;
import com.jh.auth_service.dto.LoginResponse;
import com.jh.auth_service.dto.UserRequest;
import com.jh.auth_service.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
	
	private final UserService userService;
	
	@PostMapping("/register")
	public ResponseEntity<?> registrarUser(@Valid @RequestBody UserRequest userRequest){
		userService.salvarNovoUsuario(userRequest);
		
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> realizarLogin(@RequestBody LoginRequest loginRequest){
		String token = userService.realizarLogin(loginRequest);
		LoginResponse loginResponse = new LoginResponse(token);
		
		return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
	}
}
