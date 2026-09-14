package com.jh.procedimento_service.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.jh.procedimento_service.dto.procedimento.categoria.CategoriaRequest;
import com.jh.procedimento_service.repository.CategoriaRepository;
import com.jh.procedimento_service.repository.ProcedimentoRepository;
import com.jh.procedimento_service.service.CategoriaService;

import jakarta.transaction.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class CategoriaControllerIntegrationTest {
	
	private static final String BASE_URL = "/categoria";
	
	private static final String ADMIN = "SCOPE_ADMIN";
		
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private CategoriaService categoriaService;
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private ProcedimentoRepository procedimentoRepository;
	
	private CategoriaRequest categoriaRequest;
	
	private Categoria categoria;
	
	private List<Categoria> categorias;
	
	@BeforeEach
	public void setUp() {
		categoriaRequest = new CategoriaRequest("categoria");
		categoria = new Categoria();
		categoria.setNome(categoriaRequest.nome());
		categoria.setAtivo(false);
	}
	
	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar201EDeveTerApenasUmaCategoriaExistenteAoCriarCategoria() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoriaRequest)))
		.andExpect(status().isCreated());
		
		categorias = categoriaRepository.findAll();
		
		assertEquals(categorias.size(), 1);
	}
	
	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar409EDeveTerApenasUmaCategoriaExistenteAoCriarCategoriaQuandoNomeJaExistir() throws JacksonException, Exception {
		categoriaRepository.save(categoria);
		
		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoriaRequest)))
		.andExpect(status().isConflict());
		
		categorias = categoriaRepository.findAll();
		
		assertEquals(categorias.size(), 1);
	}
	
	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar200EAtualizarCategoria() throws JacksonException, Exception {
		categoria = categoriaRepository.save(categoria);
		categoriaRequest = new CategoriaRequest("novo nome");

		mockMvc.perform(put(BASE_URL +"/"+categoria.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoriaRequest)))
		.andExpect(status().isOk());
		
		categoria = categoriaRepository.findById(categoria.getId()).get();
		
		assertEquals(categoriaRequest.nome(), categoria.getNome());
	}
	
	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar409ENaoAtualizarCategoriaQuandoNomeJaExistir() throws JacksonException, Exception {
		categoria = categoriaRepository.save(categoria);
		
		Categoria categoriaRepetida = new Categoria();
		categoriaRepetida.setNome("nome diferente");
		categoriaRepetida.setAtivo(false);
		categoriaRepetida = categoriaRepository.save(categoriaRepetida);

		mockMvc.perform(put(BASE_URL +"/"+categoriaRepetida.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(categoriaRequest)))
		.andExpect(status().isConflict());
		
		categoriaRepetida = categoriaRepository.findById(categoriaRepetida.getId()).get();
		
		assertNotEquals(categoriaRequest.nome(), categoriaRepetida.getNome());
	}
	
	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar200EAlterarAtivoDaCategoria() throws JacksonException, Exception {
		categoria = categoriaRepository.save(categoria);
		Boolean ativoDiferente = !categoria.getAtivo();

		mockMvc.perform(patch(BASE_URL +"/ativo/"+categoria.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ativoDiferente)))
		.andExpect(status().isOk());
		
		categoria = categoriaRepository.findById(categoria.getId()).get();
		
		assertEquals(ativoDiferente, categoria.getAtivo());
	}
	
	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar200ENaoExistirCategoriaAoDeletarCategoria() throws JacksonException, Exception {
		categoria = categoriaRepository.save(categoria);
				
		mockMvc.perform(delete(BASE_URL +"/"+categoria.getId()))
		.andExpect(status().isOk());
		
		
		assertTrue(categoriaRepository.findAll().isEmpty());	
	}
	
	@Test
	@WithMockUser(authorities = ADMIN)
	public void deveRetornar200EAlterarCategoriaDosProcedimentosAoDeletarCategoria() throws JacksonException, Exception {
		categoria = categoriaRepository.save(categoria);
		Procedimento procedimento = criarProcedimento(categoria);
		categoria = categoriaRepository.findById(categoria.getId()).get();
				
		mockMvc.perform(delete(BASE_URL +"/"+categoria.getId()))
		.andExpect(status().isOk());
		
		
		assertTrue(categoriaRepository.findById(categoria.getId()).isEmpty());
		
		procedimento = procedimentoRepository.findById(procedimento.getId()).get();
		assertEquals("Não Definida", procedimento.getCategoria().getNome());
	}
	
	private Procedimento criarProcedimento(Categoria categoria) {
		Procedimento procedimento = new Procedimento();
		procedimento.setAtivo(true);
		procedimento.setCategoria(categoria);
		procedimento = procedimentoRepository.save(procedimento);
		categoria.setProcedimentos(List.of(procedimento));
		
		return procedimento;
	}
}
