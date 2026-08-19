package com.jh.procedimento_service.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jh.procedimento_service.domain.Categoria;
import com.jh.procedimento_service.domain.Procedimento;
import com.jh.procedimento_service.dto.procedimento.categoria.CategoriaRequest;
import com.jh.procedimento_service.exceptions.CategoriaRepetidaException;
import com.jh.procedimento_service.repository.CategoriaRepository;
import com.jh.procedimento_service.repository.ProcedimentoRepository;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {
	@Mock
	private ProcedimentoRepository procedimentoRepository;
	@Mock
	private CategoriaRepository categoriaRepository;
	
	@InjectMocks
	private CategoriaService categoriaService;
	
	CategoriaRequest categoriaRequest;
	
	Categoria categoria;
	
	@BeforeEach
	public void setUp() {
		categoriaRequest = new CategoriaRequest("categoria");
		categoria = new Categoria();
		categoria.setId(1L);
		categoria.setNome(categoriaRequest.nome());
	}
	
	@Test
	public void deveSalvarComSucesso() {
		when(categoriaRepository.findByNome(categoriaRequest.nome()))
		.thenReturn(Optional.empty());
		
		categoriaService.criarCategoria(categoriaRequest);
		
		verify(categoriaRepository, atLeastOnce()).save(any());
	}
	
	@Test
	public void deveLancarExcecaoQuandoNomeForRepetido() {
		when(categoriaRepository.findByNome(categoriaRequest.nome()))
		.thenReturn(Optional.of(new Categoria()));
		
		assertThrows(CategoriaRepetidaException.class, () -> categoriaService.criarCategoria(categoriaRequest));
		
		verify(categoriaRepository, never()).save(any());
	}
	
	@Test
	public void deveAtualizarCategoriaComSucessoQuandoNaoNaoEncontrarNomeRepetido() {
		when(categoriaRepository.findByNome(categoriaRequest.nome()))
		.thenReturn(Optional.empty());
		
		when(categoriaRepository.findById(categoria.getId()))
		.thenReturn(Optional.of(categoria));
		
		categoriaService.atualizarCategoria(categoria.getId(), categoriaRequest);
		
		verify(categoriaRepository, atLeastOnce()).save(any());
	}
	
	@Test
	public void deveAtualizarCategoriaComSucessoQuandoACategoriaRepetidaForElaMesmo() {
		when(categoriaRepository.findByNome(categoriaRequest.nome()))
		.thenReturn(Optional.of(categoria));
		
		when(categoriaRepository.findById(categoria.getId()))
		.thenReturn(Optional.of(categoria));
		
		categoriaService.atualizarCategoria(categoria.getId(), categoriaRequest);
		
		verify(categoriaRepository, atLeastOnce()).save(any());
	}
	
	@Test
	public void deveLancarExcecaoQuandoTentaAtualizarComNomeRepetido() {
		Categoria categoriaRepetida = new Categoria();
		categoriaRepetida.setId(Long.MAX_VALUE);
		
		when(categoriaRepository.findByNome(categoriaRequest.nome()))
		.thenReturn(Optional.of(categoriaRepetida));
		
		when(categoriaRepository.findById(categoria.getId()))
		.thenReturn(Optional.of(categoria));
		
		assertThrows(CategoriaRepetidaException.class, () -> categoriaService.atualizarCategoria(categoria.getId(), categoriaRequest));
		
		verify(categoriaRepository, never()).save(any());
	}
	
	@Test
	public void deveAtualizarProcedimentosAntesDeDeletarCategoria() {
		Procedimento procedimento = new Procedimento();
		categoria.setProcedimentos(List.of(procedimento));
		
		
		Categoria categoriaNaoDefinida = new Categoria();
		categoriaNaoDefinida.setNome("Não Definida");
		
		when(categoriaRepository.findById(categoria.getId()))
		.thenReturn(Optional.of(categoria));
		
		when(categoriaRepository.findByNome(categoriaNaoDefinida.getNome()))
		.thenReturn(Optional.of(categoriaNaoDefinida));
		
		categoriaService.deletarCategoria(categoria.getId());
		
		verify(categoriaRepository, atLeastOnce()).delete(categoria);
		verify(procedimentoRepository, atLeastOnce()).saveAll(any());
	}
}
