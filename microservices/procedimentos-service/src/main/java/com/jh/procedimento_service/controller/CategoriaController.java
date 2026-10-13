package com.jh.procedimento_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jh.procedimento_service.dto.categoria.CategoriaRequest;
import com.jh.procedimento_service.dto.categoria.CategoriaResponse;
import com.jh.procedimento_service.service.CategoriaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categoria")
@RequiredArgsConstructor
@Tag(name = "Categoria", description = "Endpoints para gerenciamento das categorias dos procedimentos.")
public class CategoriaController {

	private final CategoriaService categoriaService;

	@PostMapping
	@Operation(summary = "Criar Categoria", description = "Apenas usuários com escopo de ADMIN podem criar categoria.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Categoria foi criada com sucesso"),
		@ApiResponse(responseCode = "409", description = "Categoria com o nome informado já existe")
	})
	public ResponseEntity<?> criarCategoria(@Valid @RequestBody CategoriaRequest categoriaRequest) {
		categoriaService.criarCategoria(categoriaRequest);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{id}")
	@Operation(summary = "Atualizar Categoria", description = "Apenas usuários com escopo de ADMIN podem atualizar categoria.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Categoria foi atualizada com sucesso"),
		@ApiResponse(responseCode = "401", description = "Categoria não encontrada"),
		@ApiResponse(responseCode = "409", description = "Categoria com o nome informado já existe")
	})
	public ResponseEntity<?> atualizarCategoria(@PathVariable Long id,
			@Valid @RequestBody CategoriaRequest categoriaRequest) {
		categoriaService.atualizarCategoria(id, categoriaRequest);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PatchMapping("/ativo/{id}")
	@Operation(summary = "Atualizar o ativo da categoria", description = "Apenas usuários com escopo de ADMIN podem atualizar o ativo da categoria.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Ativo da categoria foi alterado com sucesso"),
			@ApiResponse(responseCode = "401", description = "Categoria não encontrada"),
		})
	public ResponseEntity<?> atualizarAtivoDaCategoria(@PathVariable Long id, @RequestBody(required = true) Boolean ativo) {
		categoriaService.atualizarAtivo(id, ativo);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping
	@Operation(summary = "Lista todas as categorias", description = "Apenas usuários com escopo de ADMIN podem listar todas as categorias.")
	public ResponseEntity<?> listarTodas() {
		List<CategoriaResponse> categorias = categoriaService.listarTodasCategorias();

		return ResponseEntity.status(HttpStatus.OK).body(categorias);
	}

	@GetMapping("/ativas")
	@Operation(summary = "Lista todas as categorias ativas", description = "Listar todas as categorias ativas é permitido a todos")
	public ResponseEntity<?> listarTodasAtivas() {
		List<CategoriaResponse> categorias = categoriaService.listarTodasCategoriasAtivas();

		return ResponseEntity.status(HttpStatus.OK).body(categorias);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Deleta a categoria por id", description = "Apenas usuários com escopo de ADMIN podem deletar categoria. "
			+ "Quando acontece a exclusão de uma categoria, todos os procedimentos que "
			+ "apontavam para essa categoria agoram apontam para a categoria 'Não definida'")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Categoria foi deletada com sucesso"),
			@ApiResponse(responseCode = "401", description = "Categoria não encontrada"),
		})
	public ResponseEntity<?> deletarCategoria(@PathVariable Long id) {
		categoriaService.deletarCategoria(id);

		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
