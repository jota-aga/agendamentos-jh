package com.jh.auth_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jh.auth_service.dto.ErrorResponse;
import com.jh.auth_service.dto.UsuarioResponse;
import com.jh.auth_service.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Tag(name = "Usuário", description = "Gerenciamento dos Usuários")
public class UsuarioController {

	private final UsuarioService usuarioService;

	@GetMapping("/{id}")
	@Operation(summary = "Procura um usuário pelo seu id")
	@ApiResponse(responseCode = "200", description = "Retorna o DTO do usuário")
	@ApiResponse(responseCode = "404", description = "Usuário não foi encontrado", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
	public ResponseEntity<UsuarioResponse> procurarUsuarioPorId(@PathVariable Long id) {
		UsuarioResponse usuarioResponse = usuarioService.procurarUsuarioPorId(id);

		return ResponseEntity.status(HttpStatus.OK).body(usuarioResponse);
	}

	@GetMapping("/email")
	@Operation(summary = "Procura um usuário pelo seu email")
	@ApiResponse(responseCode = "200", description = "Retorna o DTO do usuário")
	@ApiResponse(responseCode = "404", description = "Usuário não foi encontrado", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
	public ResponseEntity<UsuarioResponse> procurarUsuarioEmail(@RequestParam String email) {
		UsuarioResponse usuarioResponse = usuarioService.procurarUsuarioPorEmail(email);

		return ResponseEntity.status(HttpStatus.OK).body(usuarioResponse);
	}

	@GetMapping("/nome")
	@Operation(summary = "Retorna todos os usuários que contém como parte do nome o parâmetro passado na requisição")
	@ApiResponse(responseCode = "200", description = "Retorna a lista de usuários")
	public ResponseEntity<List<UsuarioResponse>> procurarUsuarioPorNome(@RequestParam String nome) {
		List<UsuarioResponse> usuariosResponse = usuarioService.procurarUsuarioPorNome(nome);

		return ResponseEntity.status(HttpStatus.OK).body(usuariosResponse);
	}
}
