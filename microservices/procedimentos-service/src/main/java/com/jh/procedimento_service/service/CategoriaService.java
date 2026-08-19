package com.jh.procedimento_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jh.procedimento_service.domain.Categoria;
import com.jh.procedimento_service.domain.Procedimento;
import com.jh.procedimento_service.dto.procedimento.categoria.CategoriaRequest;
import com.jh.procedimento_service.dto.procedimento.categoria.CategoriaResponse;
import com.jh.procedimento_service.exceptions.CategoriaRepetidaException;
import com.jh.procedimento_service.exceptions.NaoEncontradoException;
import com.jh.procedimento_service.mappers.CategoriaMapper;
import com.jh.procedimento_service.repository.CategoriaRepository;
import com.jh.procedimento_service.repository.ProcedimentoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {
	
	private final CategoriaRepository categoriaRepository;
	
	private final ProcedimentoRepository procedimentoRepository;
	
	@Transactional
	public void criarCategoria(CategoriaRequest categoriaRequest) {
		Optional<Categoria> categoriaRepetida = categoriaRepository.findByNome(categoriaRequest.nome());
		
		if(categoriaRepetida.isPresent()) throw new CategoriaRepetidaException();
		
		Categoria categoria = new Categoria();
		categoria.setNome(categoriaRequest.nome());
		categoria.setAtivo(true);
		
		categoriaRepository.save(categoria);
	}
	
	@Transactional
	public void atualizarCategoria(Long id, CategoriaRequest categoriaRequest) {
		Optional<Categoria> optionalCategoriaRepetida = categoriaRepository.findByNome(categoriaRequest.nome());
		Categoria categoria = getCategoriaPorId(id);
		
		if(optionalCategoriaRepetida.isPresent() ) {
			Categoria categoriaRepetida = optionalCategoriaRepetida.get();
			
			if(categoriaRepetida.getId() != categoria.getId()) throw new CategoriaRepetidaException();
		}
		
		categoria.setNome(categoriaRequest.nome());
		
		categoriaRepository.save(categoria);
	}
	
	@Transactional
	public void atualizarAtivo(Long id, Boolean ativo) {
		Categoria categoria = getCategoriaPorId(id);
		categoria.setAtivo(ativo);
		
		categoriaRepository.save(categoria);
	}
	
	@Transactional
	public void deletarCategoria(Long id) {
		Categoria categoria = getCategoriaPorId(id);
		
		List<Procedimento> procedimentos = categoria.getProcedimentos();
		
		if(!procedimentos.isEmpty()) {
			Categoria categoriaNaoDefinida = getCategoriaNaoDefinida();
			
			procedimentos.forEach(p -> p.setCategoria(categoriaNaoDefinida));
			procedimentoRepository.saveAll(procedimentos);
		}
		
		categoriaRepository.delete(categoria);
	}
	
	public List<CategoriaResponse> listarTodasCategorias(){
		List<CategoriaResponse> response = CategoriaMapper.INSTANCE.listEntityToListDTO(categoriaRepository.findAll());
		return response;
	}
	
	public List<CategoriaResponse> listarTodasCategoriasAtivas(){
		List<CategoriaResponse> response = CategoriaMapper.INSTANCE.listEntityToListDTO(categoriaRepository.findAllByAtivoTrue());
		return response;
	}

	private Categoria getCategoriaPorId(Long id) {
		return categoriaRepository.findById(id)
				.orElseThrow(() -> new NaoEncontradoException("Categoria por id"));
	}
	
	private Categoria getCategoriaNaoDefinida() {
		String nomeCategoriaNaoDefinida = "Não Definida";
		
		Optional<Categoria> optionalCategoria = categoriaRepository.findByNome(nomeCategoriaNaoDefinida);
		
		if(optionalCategoria.isEmpty()) {
			Categoria categoria = new Categoria();
			categoria.setNome(nomeCategoriaNaoDefinida);
			categoria.setAtivo(true);
			return categoriaRepository.save(categoria);
		}
		else {
			return optionalCategoria.get();
		}
	}
}
