package com.jh.procedimento_service.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jh.procedimento_service.domain.Categoria;
import com.jh.procedimento_service.domain.Procedimento;
import com.jh.procedimento_service.dto.procedimento.ProcedimentoRequest;
import com.jh.procedimento_service.exceptions.NaoEncontradoException;
import com.jh.procedimento_service.repository.CategoriaRepository;
import com.jh.procedimento_service.repository.ProcedimentoRepository;

@ExtendWith(MockitoExtension.class)
public class ProcedimentoServiceTest {
	
	@Mock
	private ProcedimentoRepository procedimentoRepository;
	
	@Mock
	private CategoriaRepository categoriaRepository;
	
	@InjectMocks
	private ProcedimentoService procedimentoService;
	
	private ProcedimentoRequest request;
	
	private Procedimento procedimento;
	
	private Categoria categoria;
	
	@BeforeEach
	public void setUp() {
		request = new ProcedimentoRequest("titulo", "descricao", BigDecimal.ONE, 30, 1L);
		categoria = new Categoria();
		categoria.setNome("nome");
		procedimento = new Procedimento(1L, request.titulo(), request.descricao(), request.preco(), 
				request.duracaoEmMinutos(), true, LocalDateTime.now(), LocalDateTime.now(), categoria);
	}
	
	@Test
	public void deveCriarProcedimentoComSucesso() {
		when(categoriaRepository.findById(request.categoriaId())).thenReturn(Optional.of(categoria));
		
		procedimentoService.criarProcedimento(request);
		
		verify(procedimentoRepository, atLeastOnce()).save(any());
	}
	
	@Test
	public void deveLancarExcecaoNaCriacaoQuandoNaCCategoriaNaoEncontrada() {
		when(categoriaRepository.findById(request.categoriaId())).thenReturn(Optional.empty());
				
		assertThrows(NaoEncontradoException.class, () -> procedimentoService.criarProcedimento(request));
		
		verify(procedimentoRepository, never()).save(any());
	}
	
	@Test
	public void deveAtualizarProcedimentoComSucesso() {
		when(procedimentoRepository.findById(procedimento.getId())).thenReturn(Optional.of(procedimento));
		when(categoriaRepository.findById(request.categoriaId())).thenReturn(Optional.of(categoria));
		
		procedimentoService.atualizarProcedimento(procedimento.getId(),request);
		
		verify(procedimentoRepository, atLeastOnce()).save(any());
	}
	
	@Test
	public void deveLancarExcecaoNaAtualizacaoQuandoProcedimentoNaoEncontrada() {
		when(procedimentoRepository.findById(procedimento.getId())).thenReturn(Optional.empty());
				
		assertThrows(NaoEncontradoException.class, () -> procedimentoService.atualizarProcedimento(procedimento.getId(), request));
		
		verify(procedimentoRepository, never()).save(any());
	}
	
	@Test
	public void deveLancarExcecaoNaAtualizacaoQuandoCategoriaNaoEncontrada() {
		when(procedimentoRepository.findById(procedimento.getId())).thenReturn(Optional.of(procedimento));
		when(categoriaRepository.findById(request.categoriaId())).thenReturn(Optional.empty());
				
		assertThrows(NaoEncontradoException.class, () -> procedimentoService.atualizarProcedimento(procedimento.getId(), request));
		
		verify(procedimentoRepository, never()).save(any());
	}
	
	@Test
	public void devealterarAtivoComSucesso() {
		when(procedimentoRepository.findById(procedimento.getId())).thenReturn(Optional.of(procedimento));
		
		procedimentoService.alterarAtivo(procedimento.getId(), true);
		
		verify(procedimentoRepository, atLeastOnce()).save(any());
	}
	
	@Test
	public void deveLancarExcecaoNaAlteracaoDoAtivoQuandoProcedimentoNaoEncontrada() {
		when(procedimentoRepository.findById(procedimento.getId())).thenReturn(Optional.empty());
				
		assertThrows(NaoEncontradoException.class, () -> procedimentoService.alterarAtivo(procedimento.getId(), true));
		
		verify(procedimentoRepository, never()).save(any());
	}
	
	@Test
	public void deveDeletarProcedimentoComSucesso() {
		when(procedimentoRepository.findById(procedimento.getId())).thenReturn(Optional.of(procedimento));
		
		procedimentoService.deletarProcedimento(procedimento.getId());
		
		verify(procedimentoRepository, atLeastOnce()).delete(any());
	}
	
	@Test
	public void deveLancarExcecaoNaDelecaoQuandoProcedimentoNaoEnconrtado() {
		when(procedimentoRepository.findById(procedimento.getId())).thenReturn(Optional.empty());
				
		assertThrows(NaoEncontradoException.class, () -> procedimentoService.deletarProcedimento(procedimento.getId()));
		
		verify(procedimentoRepository, never()).save(any());
	}
}
