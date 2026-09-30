package com.jh.agendamento_service.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.jh.agendamento_service.domain.Agendamento;
import com.jh.agendamento_service.dto.AgendamentoAdminRequest;
import com.jh.agendamento_service.dto.AgendamentoStatusRequest;
import com.jh.agendamento_service.dto.CategoriaResponse;
import com.jh.agendamento_service.dto.ProcedimentoResponse;
import com.jh.agendamento_service.enums.AgendamentoStatus;
import com.jh.agendamento_service.repository.AgendamentoRepository;
import com.jh.agendamento_service.service.ProcedimentoExternalService;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class AgendamentoAdminControllerTest {
	
	private final String BASE_URL = "/agendamento/admin";
	
	private final String admin = "SCOPE_ADMIN";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockitoBean
	private ProcedimentoExternalService procedimentoExternalService;
	
	@Autowired
	private AgendamentoRepository agendamentoRepository;
	
	private AgendamentoAdminRequest agendamentoAdminRequest;
	
	private LocalDate data;
	
	private LocalTime horario;
	
	private Agendamento agendamento;
	
	private ProcedimentoResponse procedimentoResponse;
	
	@BeforeEach
	public void setUp() {
		agendamentoRepository.deleteAll();
		
		data = LocalDate.now().plusDays(2);
		horario = LocalTime.of(14, 0);
		procedimentoResponse = new ProcedimentoResponse(1L, "titulo", "descrição", BigDecimal.TEN,
				30, true, new CategoriaResponse(1L, "nome", true));
		when(procedimentoExternalService.procurarProcedimentoPorId(1L)).thenReturn(procedimentoResponse);
		
		agendamentoAdminRequest = new AgendamentoAdminRequest(1L, "usuario", data, horario, AgendamentoStatus.AGENDADO, 1L);
	}
	
	@Test
	@WithMockUser(authorities = admin)
	public void deveCriarAgendamentoComSucesso() throws JacksonException, Exception {
		
		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoAdminRequest)))
		.andExpect(status().isCreated());
		
		List<Agendamento> agendamentos = agendamentoRepository.findAll();
		
		assertEquals(1, agendamentos.size());
		
		agendamento = agendamentos.get(0);
		
		assertEquals(agendamentoAdminRequest.usuarioId(), agendamento.getUsuarioId());
		assertEquals(agendamentoAdminRequest.nomeDoUsuario(), agendamento.getNomeDoUsuario());
		assertEquals(agendamentoAdminRequest.data(), agendamento.getData());
		assertEquals(agendamentoAdminRequest.inicio(), agendamento.getInicio());
		assertEquals(agendamentoAdminRequest.inicio().plusMinutes(30), agendamento.getFim());
		assertEquals(agendamentoAdminRequest.status(), agendamento.getStatus());
		assertEquals(procedimentoResponse.titulo(), agendamento.getTituloDoProcedimento());
		assertEquals(procedimentoResponse.preco(), agendamento.getPreco());
		assertEquals(procedimentoResponse.duracaoEmMinutos(), agendamento.getDuracaoEmMinutos());
		assertEquals(procedimentoResponse.categoria().nome(), agendamento.getNomeDaCategoria());
	}
	
	@Test
	@WithMockUser(authorities = admin)
	public void naoDeveCriarAgendamentoQuandoHouverConflito() throws JacksonException, Exception {
		agendamento = Agendamento.builder()
				.usuarioId(2L)
				.nomeDoUsuario("usuario diferente")
				.data(data)
				.inicio(horario)
				.fim(horario.plusMinutes(30))
				.tituloDoProcedimento("procedimento diferente")
				.preco(BigDecimal.ONE)
				.duracaoEmMinutos(50)
				.nomeDaCategoria("categoria diferente")
				.status(AgendamentoStatus.AGENDADO)
				.build();
		agendamento = agendamentoRepository.save(agendamento);
		
		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoAdminRequest)))
		.andExpect(status().isConflict());
		
		List<Agendamento> agendamentos = agendamentoRepository.findAll();
		
		assertEquals(1, agendamentos.size());
	}
	
	@Test
	@WithMockUser(authorities = admin)
	public void deveAtualizarAgendamentoComSucesso() throws JacksonException, Exception {
		agendamento = Agendamento.builder()
				.usuarioId(2L)
				.nomeDoUsuario("usuario diferente")
				.data(LocalDate.now())
				.inicio(LocalTime.now())
				.fim(LocalTime.now().plusMinutes(50))
				.tituloDoProcedimento("procedimento diferente")
				.preco(BigDecimal.ONE)
				.duracaoEmMinutos(50)
				.nomeDaCategoria("categoria diferente")
				.status(AgendamentoStatus.AGENDADO)
				.build();
		agendamento = agendamentoRepository.save(agendamento);
		
		mockMvc.perform(put(BASE_URL+"/"+agendamento.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoAdminRequest)))
		.andExpect(status().isOk());
		
		agendamento = agendamentoRepository.findById(agendamento.getId()).get();
		
		assertEquals(agendamentoAdminRequest.usuarioId(), agendamento.getUsuarioId());
		assertEquals(agendamentoAdminRequest.nomeDoUsuario(), agendamento.getNomeDoUsuario());
		assertEquals(agendamentoAdminRequest.data(), agendamento.getData());
		assertEquals(agendamentoAdminRequest.inicio(), agendamento.getInicio());
		assertEquals(agendamentoAdminRequest.inicio().plusMinutes(30), agendamento.getFim());
		assertEquals(agendamentoAdminRequest.status(), agendamento.getStatus());
		assertEquals(procedimentoResponse.titulo(), agendamento.getTituloDoProcedimento());
		assertEquals(procedimentoResponse.preco(), agendamento.getPreco());
		assertEquals(procedimentoResponse.duracaoEmMinutos(), agendamento.getDuracaoEmMinutos());
		assertEquals(procedimentoResponse.categoria().nome(), agendamento.getNomeDaCategoria());
	}
	
	@Test
	@WithMockUser(authorities = admin)
	public void naoDeveAtualizarQuandoHouverConflito() throws JacksonException, Exception {
		agendamento = Agendamento.builder()
				.usuarioId(2L)
				.nomeDoUsuario("usuario diferente")
				.data(data)
				.inicio(horario)
				.fim(horario.plusMinutes(30))
				.tituloDoProcedimento("procedimento diferente")
				.preco(BigDecimal.ONE)
				.duracaoEmMinutos(50)
				.nomeDaCategoria("categoria diferente")
				.status(AgendamentoStatus.AGENDADO)
				.build();
		agendamento = agendamentoRepository.save(agendamento);
		
		Agendamento agendamentoASerAtualizado = Agendamento.builder()
				.usuarioId(2L)
				.nomeDoUsuario("usuario diferente")
				.data(LocalDate.now())
				.inicio(LocalTime.now())
				.fim(LocalTime.now().plusMinutes(50))
				.tituloDoProcedimento("procedimento diferente")
				.preco(BigDecimal.ONE)
				.duracaoEmMinutos(50)
				.nomeDaCategoria("categoria diferente")
				.status(AgendamentoStatus.CANCELADO)
				.build();
		agendamentoASerAtualizado = agendamentoRepository.save(agendamentoASerAtualizado);
		
		mockMvc.perform(put(BASE_URL+"/"+agendamentoASerAtualizado.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoAdminRequest)))
		.andExpect(status().isConflict());
		
		agendamento = agendamentoRepository.findById(agendamento.getId()).get();
		
		assertNotEquals(agendamentoAdminRequest.nomeDoUsuario(), agendamentoASerAtualizado.getNomeDoUsuario());
		assertNotEquals(agendamentoAdminRequest.usuarioId(), agendamentoASerAtualizado.getUsuarioId());
		assertNotEquals(agendamentoAdminRequest.data(), agendamentoASerAtualizado.getData());
		assertNotEquals(agendamentoAdminRequest.inicio(), agendamentoASerAtualizado.getInicio());
		assertNotEquals(agendamentoAdminRequest.inicio().plusMinutes(30), agendamentoASerAtualizado.getFim());
		assertNotEquals(agendamentoAdminRequest.status(), agendamentoASerAtualizado.getStatus());
		assertNotEquals(procedimentoResponse.titulo(), agendamentoASerAtualizado.getTituloDoProcedimento());
		assertNotEquals(procedimentoResponse.preco(), agendamentoASerAtualizado.getPreco());
		assertNotEquals(procedimentoResponse.duracaoEmMinutos(), agendamentoASerAtualizado.getDuracaoEmMinutos());
		assertNotEquals(procedimentoResponse.categoria().nome(), agendamentoASerAtualizado.getNomeDaCategoria());
	}
	
	@Test
	@WithMockUser(authorities = admin)
	public void deveAlterarStatusDoAgendamento() throws JacksonException, Exception {
		agendamento = Agendamento.builder()
				.usuarioId(2L)
				.nomeDoUsuario("usuario diferente")
				.data(LocalDate.now())
				.inicio(LocalTime.now())
				.fim(LocalTime.now().plusMinutes(50))
				.tituloDoProcedimento("procedimento diferente")
				.preco(BigDecimal.ONE)
				.duracaoEmMinutos(50)
				.nomeDaCategoria("categoria diferente")
				.status(AgendamentoStatus.AGENDADO)
				.build();
		agendamento = agendamentoRepository.save(agendamento);
		
		AgendamentoStatusRequest agendamentoStatusRequest = new AgendamentoStatusRequest(AgendamentoStatus.CONCLUIDO);
		
		mockMvc.perform(patch(BASE_URL+"/"+agendamento.getId()+"/status")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoStatusRequest)))
		.andExpect(status().isOk());
		
		agendamento = agendamentoRepository.findById(agendamento.getId()).get();
		
		assertEquals(agendamentoStatusRequest.status(), agendamento.getStatus());
	}
}
