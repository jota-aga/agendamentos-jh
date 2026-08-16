package com.jh.procedimento_service.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jh.procedimento_service.domain.Categoria;
import com.jh.procedimento_service.dto.CategoriaRequest;
import com.jh.procedimento_service.exceptions.CategoriaRepetidaException;
import com.jh.procedimento_service.exceptions.NaoEncontradoException;
import com.jh.procedimento_service.repository.CategoriaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {
	
	private final CategoriaRepository categoriaRepository;
	
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

	private Categoria getCategoriaPorId(Long id) {
		return categoriaRepository.findById(id)
				.orElseThrow(() -> new NaoEncontradoException("Categoria por id"));
	}
}
