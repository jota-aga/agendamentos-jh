package com.jh.procedimento_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jh.procedimento_service.domain.Categoria;
import com.jh.procedimento_service.domain.Procedimento;
import com.jh.procedimento_service.dto.procedimento.ProcedimentoRequest;
import com.jh.procedimento_service.dto.procedimento.ProcedimentoResponse;
import com.jh.procedimento_service.exceptions.NaoEncontradoException;
import com.jh.procedimento_service.mappers.ProcedimentoMapper;
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
		procedimento = ProcedimentoMapper.INSTANCE.requestToEntity(procedimentoRequest);
		procedimento.setCriadoEm(LocalDateTime.now());
		procedimento.setAtualizadoEm(LocalDateTime.now());
		procedimento.setAtivo(true);
		procedimento.setCategoria(categoria);
		
		procedimentoRepository.save(procedimento);
	}
	
	@Transactional
	public void atualizarProcedimento(Long id, ProcedimentoRequest procedimentoRequest) {
		Procedimento procedimento = procurarProcedimentoPorId(id);;
		
		Categoria categoria = procurarCategoriaPorId(procedimentoRequest.categoriaId());
		
		ProcedimentoMapper.INSTANCE.updateEntity(procedimentoRequest, procedimento);
		procedimento.setAtualizadoEm(LocalDateTime.now());
		procedimento.setCategoria(categoria);
		
		procedimentoRepository.save(procedimento);
	}
	
	@Transactional
	public void alterarAtivo(Long id, Boolean ativo) {
		Procedimento procedimento = procurarProcedimentoPorId(id);;
		
		procedimento.setAtivo(ativo);
		
		procedimentoRepository.save(procedimento);
	}
	
	public List<ProcedimentoResponse> procurarTodosProcedimentos(){
		List<ProcedimentoResponse> response = ProcedimentoMapper.INSTANCE
				.listEntityToListReponse(procedimentoRepository.findAll());
		
		return response;
	}
	
	public List<ProcedimentoResponse> procurarProcedimentosAtivos(){
		List<ProcedimentoResponse> response = ProcedimentoMapper.INSTANCE
				.listEntityToListReponse(procedimentoRepository.findAllByAtivoTrue());
		
		return response;
	}
	
	public ProcedimentoResponse getProcedimentoPorId(Long id) {
		ProcedimentoResponse response = ProcedimentoMapper.INSTANCE
				.entityToResponse(procurarProcedimentoPorId(id));
		return response;
	}
	
	public void deletarProcedimento(Long id) {
		Procedimento procedimento = procurarProcedimentoPorId(id);
		
		procedimentoRepository.delete(procedimento);
	}
	
	private Procedimento procurarProcedimentoPorId(Long id) {
		return procedimentoRepository.findById(id)
				.orElseThrow(() -> new NaoEncontradoException("Procedimento por id"));
	}
	
	private Categoria procurarCategoriaPorId(Long categoriaId) {
		return categoriaRepository.findById(categoriaId)
				.orElseThrow(() -> new NaoEncontradoException("Categoria por id"));
	}
}
