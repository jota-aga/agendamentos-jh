package com.jh.procedimento_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jh.procedimento_service.domain.Categoria;
import com.jh.procedimento_service.domain.Procedimento;
import com.jh.procedimento_service.dto.ProcedimentoRequest;
import com.jh.procedimento_service.exceptions.NaoEncontradoException;
import com.jh.procedimento_service.repository.CategoriaRepository;
import com.jh.procedimento_service.repository.ProcedimentoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProcedimentoService {
	
	private final ProcedimentoRepository procedimentoRepository;
	
	private final CategoriaRepository categoriaRepository;
	
	@Transactional
	public void criarProcedimento(ProcedimentoRequest procedimentoRequest) {
		Categoria categoria = procurarCategoriaPorId(procedimentoRequest.categoriaId());
		
		Procedimento procedimento = new Procedimento();
		procedimento.setTitulo(procedimentoRequest.titulo());
		procedimento.setDescricao(procedimentoRequest.descricao());
		procedimento.setPreco(procedimentoRequest.preco());
		procedimento.setDuracaoEmMinutos(procedimentoRequest.duracaoEmMinutos());
		procedimento.setCriadoEm(LocalDateTime.now());
		procedimento.setAtualizadoEm(LocalDateTime.now());
		procedimento.setAtivo(true);
		procedimento.setCategoria(categoria);
		
		procedimentoRepository.save(procedimento);
	}
	
	@Transactional
	public void atualizarProcedimento(ProcedimentoRequest procedimentoRequest) {
		Categoria categoria = procurarCategoriaPorId(procedimentoRequest.categoriaId());
		
		Procedimento procedimento = new Procedimento();
		procedimento.setTitulo(procedimentoRequest.titulo());
		procedimento.setDescricao(procedimentoRequest.descricao());
		procedimento.setPreco(procedimentoRequest.preco());
		procedimento.setDuracaoEmMinutos(procedimentoRequest.duracaoEmMinutos());
		procedimento.setAtualizadoEm(LocalDateTime.now());
		procedimento.setCategoria(categoria);
		
		procedimentoRepository.save(procedimento);
	}
	
	@Transactional
	public void mudarAtivo(Long id, Boolean ativo) {
		Procedimento procedimento = procurarPorId(id);
		
		procedimento.setAtivo(ativo);
		procedimentoRepository.save(procedimento);
	}
	
	public List<Procedimento> procurarTodos(){
		return procedimentoRepository.findAllByAtivoTrue();
	}
	
	public List<Procedimento> procurarAtivos(){
		return procedimentoRepository.findAllByAtivoTrue();
	}
	
	private Procedimento procurarPorId(Long id) {
		return procedimentoRepository.findById(id)
				.orElseThrow(() -> new NaoEncontradoException("Procedimento por id"));
	}
	
	private Categoria procurarCategoriaPorId(Long categoriaId) {
		return categoriaRepository.findById(categoriaId)
				.orElseThrow(() -> new NaoEncontradoException("Categoria por id"));
	}
}
