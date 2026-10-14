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

import com.jh.procedimento_service.dto.AtivoRequest;
import com.jh.procedimento_service.dto.procedimento.ProcedimentoRequest;
import com.jh.procedimento_service.dto.procedimento.ProcedimentoResponse;
import com.jh.procedimento_service.service.ProcedimentoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/procedimento")
@RequiredArgsConstructor
@Tag(name = "Procedimento", description = "Endpoints para gerenciamento dos procedimentos")
public class ProcedimentoController {
	
	private final ProcedimentoService procedimentoService;
	
	@PostMapping
	@Operation(summary = "Criar Procedimento", description = "Apenas usuários com escopo de ADMIN podem criar procedimento.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Procedimento criado com sucesso"),
			@ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
			@ApiResponse(responseCode = "500", description = "Request body inválido")
	})
	public ResponseEntity<?> criarProcedimento(@Valid @RequestBody ProcedimentoRequest procedimentoRequest){
		procedimentoService.criarProcedimento(procedimentoRequest);
		
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PutMapping("/{id}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Procedimento atualizado com sucesso"),
			@ApiResponse(responseCode = "404", description = "Categoria não encontrada ou Procedimento não encontrado"),
			@ApiResponse(responseCode = "500", description = "Request body inválido"),
	})
	@Operation(summary = "Atualizar Procedimento", description = "Apenas usuários com escopo de ADMIN podem atualizar procedimento.")
	public ResponseEntity<?> atualizarProcedimento(@PathVariable Long id, @Valid @RequestBody ProcedimentoRequest procedimentoRequest){
		procedimentoService.atualizarProcedimento(id, procedimentoRequest);
		
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PatchMapping("/ativo/{id}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Ativo do procedimento atualizado com sucesso"),
			@ApiResponse(responseCode = "500", description = "Request body inválido"),
			@ApiResponse(responseCode = "404", description = "Procedimento não encontrado"),
			
	})
	@Operation(summary = "Alterar ativo Procedimento", description = "Apenas usuários com escopo de ADMIN podem alterar ativo do procedimento.")
	public ResponseEntity<?> alterarAtivo(@PathVariable Long id, @RequestBody AtivoRequest ativo){
		procedimentoService.alterarAtivo(id, ativo);
		
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@GetMapping
	@Operation(summary = "Listar todos os procedimentos", description = "Apenas usuários com escopo de ADMIN podem listar todos os procedimentos.")
	public ResponseEntity<?> listarTodos(){
		List<ProcedimentoResponse> response = procedimentoService.procurarTodosProcedimentos();
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping("/ativos")
	@Operation(summary = "Lista todos os procedimentos ativos", description = "Listar procedimentos ativos é permitido para todos")
	public ResponseEntity<?> listarAtivos(){
		List<ProcedimentoResponse> response = procedimentoService.procurarProcedimentosAtivos();
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping("/{id}")
	@Operation(summary = "Procura um procedimento por id", description = "Apenas usuários com escopo de ADMIN podem procurar procedimento por id.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Procedimento encontrado com sucesso"),
			@ApiResponse(responseCode = "404", description = "Procedimento não encontrado")
			
	})
	public ResponseEntity<?> getProcedimentoPorId(@PathVariable Long id){
		ProcedimentoResponse response = procedimentoService.getProcedimentoPorId(id);
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping("/ativo/{id}")
	@Operation(summary = "Procura um procedimento por id e que esteja ativo", description = "Procurar Procedimento ativo por id é permitido para todos")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Procedimento encontrado com sucesso"),
			@ApiResponse(responseCode = "404", description = "Procedimento não encontrado")
			
	})
	public ResponseEntity<?> getProcedimentoPorIdEAtivo(@PathVariable Long id){
		ProcedimentoResponse response = procedimentoService.getProcedimentoPorIdEAtivo(id);
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@DeleteMapping("/{id}")
	@Operation(summary = "Deleta um procedimento", description = "Apenas usuários com escopo de ADMIN podem deletar procedimento.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Procedimento deletado com sucesso"),
			@ApiResponse(responseCode = "404", description = "Procedimento não encontrado"),
			
	})
	public ResponseEntity<?> deletarProcedimento(@PathVariable Long id){
		procedimentoService.deletarProcedimento(id);
		
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
