package com.jh.auth_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jh.auth_service.dto.UsuarioResponse;
import com.jh.auth_service.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	@GetMapping("/{id}")
	public ResponseEntity<UsuarioResponse> procurarUsuarioPorId(@PathVariable Long id){
		UsuarioResponse usuarioResponse = userService.procurarUsuarioPorId(id);
		
		return ResponseEntity.status(HttpStatus.OK).body(usuarioResponse);
	}
}
