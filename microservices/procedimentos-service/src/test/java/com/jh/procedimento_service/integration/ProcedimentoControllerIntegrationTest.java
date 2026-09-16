package com.jh.procedimento_service.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.jh.procedimento_service.domain.Categoria;
import com.jh.procedimento_service.domain.Procedimento;
import com.jh.procedimento_service.dto.procedimento.ProcedimentoRequest;
import com.jh.procedimento_service.repository.CategoriaRepository;
import com.jh.procedimento_service.repository.ProcedimentoRepository;

import jakarta.transaction.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ProcedimentoControllerIntegrationTest {
	
	private static final String BASE_URL = "/procedimento";
	
	private static final String SCOPE_ADMIN = "SCOPE_ADMIN";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ProcedimentoRepository procedimentoRepository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	private ProcedimentoRequest procedimentoRequest;
	
	private Categoria categoria;
	
	@BeforeEach
	public void setUp() {
		procedimentoRepository.deleteAll();
		categoriaRepository.deleteAll();
		
		categoria = new Categoria();
		categoria.setNome("categoria");
		categoria.setAtivo(true);
		categoria = categoriaRepository.save(categoria);
		
		procedimentoRequest = new ProcedimentoRequest("titulo", "descrição", BigDecimal.TEN, 20, categoria.getId());
	}
	
	@Test
	@WithMockUser(authorities = SCOPE_ADMIN)
	public void deveSalvarProcedimentoERetornar201() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(procedimentoRequest)))
		.andExpect(status().isCreated());
		
		List<Procedimento> procedimentos = procedimentoRepository.findAll();
		assertEquals(1, procedimentos.size());
		
		Procedimento procedimento = procedimentos.get(0);
		
		assertEquals(procedimentoRequest.titulo(), procedimento.getTitulo());
		assertEquals(procedimentoRequest.descricao(), procedimento.getDescricao());
		assertEquals(procedimentoRequest.preco(), procedimento.getPreco());
		assertEquals(procedimentoRequest.duracaoEmMinutos(), procedimento.getDuracaoEmMinutos());
		assertEquals(procedimentoRequest.categoriaId(), procedimento.getCategoria().getId());
		assertEquals(true, procedimento.getAtivo());
	}
	
	@Test
	@WithMockUser(authorities = SCOPE_ADMIN)
	public void deveRetornar400ENaoDeveSalvarQuandoCategoriaNaoForEncontradaAoCriarProcedimento() throws JacksonException, Exception {
		procedimentoRequest = new ProcedimentoRequest("titulo", "descrição", BigDecimal.TEN, 20, Long.MAX_VALUE);

		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(procedimentoRequest)))
		.andExpect(status().isNotFound());
		
		List<Procedimento> procedimentos = procedimentoRepository.findAll();
		assertEquals(0, procedimentos.size());
	}
	
	@Test
	@WithMockUser(authorities = SCOPE_ADMIN)
	public void deveAtualizarProcedimentoERetornar201() throws JacksonException, Exception {
		ProcedimentoRequest novoRequest = new ProcedimentoRequest("titulo diferente", "descrição diferente", BigDecimal.ZERO, 10, categoria.getId());
		Procedimento procedimento = criarProcedimento();
		
		mockMvc.perform(put(BASE_URL+"/"+procedimento.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(novoRequest)))
		.andExpect(status().isOk());
		
		List<Procedimento> procedimentos = procedimentoRepository.findAll();
		assertEquals(1, procedimentos.size());
		
		procedimento = procedimentos.get(0);
		
		assertEquals(novoRequest.titulo(), procedimento.getTitulo());
		assertEquals(novoRequest.descricao(), procedimento.getDescricao());
		assertEquals(novoRequest.preco(), procedimento.getPreco());
		assertEquals(novoRequest.duracaoEmMinutos(), procedimento.getDuracaoEmMinutos());
		assertEquals(novoRequest.categoriaId(), procedimento.getCategoria().getId());
		assertEquals(true, procedimento.getAtivo());
	}
	
	@Test
	@WithMockUser(authorities = SCOPE_ADMIN)
	public void deveRetornar400ENaoDeveAtualizarQuandoCategoriaNaoForEncontradaAoAtualizarProcedimento() throws JacksonException, Exception {
		Procedimento procedimento = criarProcedimento();
		procedimentoRequest = new ProcedimentoRequest("diferente ", "diferente", BigDecimal.ZERO, 0, Long.MAX_VALUE);

		mockMvc.perform(put(BASE_URL+"/"+procedimento.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(procedimentoRequest)))
		.andExpect(status().isNotFound());
		
		List<Procedimento> procedimentos = procedimentoRepository.findAll();
		procedimento = procedimentos.get(0);
		
		assertNotEquals(procedimentoRequest.titulo(), procedimento.getTitulo());
		assertNotEquals(procedimentoRequest.descricao(), procedimento.getDescricao());
		assertNotEquals(procedimentoRequest.preco(), procedimento.getPreco());
		assertNotEquals(procedimentoRequest.duracaoEmMinutos(), procedimento.getDuracaoEmMinutos());
		assertNotEquals(procedimentoRequest.categoriaId(), procedimento.getCategoria().getId());
	}
	
	@Test
	@WithMockUser(authorities = SCOPE_ADMIN)
	public void deveAlterarAtivoERetornar200() throws JacksonException, Exception {
		Procedimento procedimento = criarProcedimento();

		mockMvc.perform(patch(BASE_URL+"/ativo/"+procedimento.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(false)))
		.andExpect(status().isOk());
		
		procedimento = procedimentoRepository.findById(procedimento.getId()).get();
		
		assertEquals(false, procedimento.getAtivo());
	}
	
	@Test
	@WithMockUser(authorities = SCOPE_ADMIN)
	public void deveRetornar201ENaoDeveAtualizarQuandoProcedimentoNaoForEncontradoAoAlterarAtivo() throws JacksonException, Exception {
		Procedimento procedimento = criarProcedimento();

		mockMvc.perform(patch(BASE_URL+"/ativo/"+Long.MAX_VALUE)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(false)))
		.andExpect(status().isNotFound());
		
		procedimento = procedimentoRepository.findById(procedimento.getId()).get();
		
		assertNotEquals(false, procedimento.getAtivo());
	}
	
	@Test
	@WithMockUser(authorities = SCOPE_ADMIN)
	public void deveDeletarOProcedimentoERetornar200() throws JacksonException, Exception {
		Procedimento procedimento = criarProcedimento();

		mockMvc.perform(delete(BASE_URL+"/"+procedimento.getId()))
		.andExpect(status().isOk());
		
		List<Procedimento> procedimentos = procedimentoRepository.findAll();
		assertTrue(procedimentos.isEmpty());
	}
	
	private Procedimento criarProcedimento() {
		Procedimento procedimento = new Procedimento();
		
		procedimento.setTitulo("titulo");
		procedimento.setDescricao("descrição");
		procedimento.setCriadoEm(LocalDateTime.now());
		procedimento.setAtualizadoEm(LocalDateTime.now());
		procedimento.setAtivo(true);
		procedimento.setCategoria(categoria);
		procedimento.setDuracaoEmMinutos(10);
		procedimento.setPreco(BigDecimal.TEN);
		
		
		return procedimentoRepository.save(procedimento);
	}
}
