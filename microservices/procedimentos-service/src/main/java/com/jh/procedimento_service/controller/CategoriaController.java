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

import com.jh.procedimento_service.dto.procedimento.categoria.CategoriaRequest;
import com.jh.procedimento_service.dto.procedimento.categoria.CategoriaResponse;
import com.jh.procedimento_service.service.CategoriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categoria")
@RequiredArgsConstructor
public class CategoriaController {
	
	private final CategoriaService categoriaService;
	
	@PostMapping
	public ResponseEntity<?> criarCategoria(@Valid @RequestBody CategoriaRequest categoriaRequest){
		categoriaService.criarCategoria(categoriaRequest);
		
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> atualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaRequest categoriaRequest){
		categoriaService.atualizarCategoria(id, categoriaRequest);
		
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PatchMapping("/ativo/{id}")
	public ResponseEntity<?> mudarAtivoDaCategoria(@PathVariable Long id, @RequestBody Boolean ativo){
		categoriaService.atualizarAtivo(id, ativo);
		
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@GetMapping
	public ResponseEntity<?> listarTodas(){
		List<CategoriaResponse> categorias = categoriaService.listarTodasCategorias();
		
		return ResponseEntity.status(HttpStatus.OK).body(categorias);
	}
	
	@GetMapping("/ativas")
	public ResponseEntity<?> listarTodasAtivas(){
		List<CategoriaResponse> categorias = categoriaService.listarTodasCategoriasAtivas();
		
		return ResponseEntity.status(HttpStatus.OK).body(categorias);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletarCategoria(@PathVariable Long id){
		categoriaService.deletarCategoria(id);
		
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
