package com.jh.auth_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jh.auth_service.dto.UsuarioResponse;
import com.jh.auth_service.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {
	
	private final UsuarioService usuarioService;
	
	@GetMapping("/{id}")
	public ResponseEntity<UsuarioResponse> procurarUsuarioPorId(@PathVariable Long id){
		UsuarioResponse usuarioResponse = usuarioService.procurarUsuarioPorId(id);
		
		return ResponseEntity.status(HttpStatus.OK).body(usuarioResponse);
	}
	
	@GetMapping("/email")
	public ResponseEntity<UsuarioResponse> procurarUsuarioEmail(@RequestParam String email){
		UsuarioResponse usuarioResponse = usuarioService.procurarUsuarioPorEmail(email);
		
		return ResponseEntity.status(HttpStatus.OK).body(usuarioResponse);
	}
	
	@GetMapping("/nome")
	public ResponseEntity<List<UsuarioResponse>> procurarUsuarioPorNome(@RequestParam String nome){
		List<UsuarioResponse> usuariosResponse = usuarioService.procurarUsuarioPorNome(nome);
		
		return ResponseEntity.status(HttpStatus.OK).body(usuariosResponse);
	}
}
