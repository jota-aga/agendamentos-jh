package com.jh.agendamento_service.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.jh.agendamento_service.dto.AgendamentoRequest;
import com.jh.agendamento_service.dto.CategoriaResponse;
import com.jh.agendamento_service.dto.ProcedimentoResponse;
import com.jh.agendamento_service.dto.UsuarioAutenticadoDTO;
import com.jh.agendamento_service.enums.AgendamentoStatus;
import com.jh.agendamento_service.repository.AgendamentoRepository;
import com.jh.agendamento_service.service.ProcedimentoExternalService;
import com.jh.agendamento_service.service.SecurityService;

import jakarta.transaction.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AgendamentoControllerTest {

	private final String BASE_URL = "/agendamento";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AgendamentoRepository agendamentoRepository;

	@MockitoBean
	private ProcedimentoExternalService procedimentoExternalService;

	@MockitoBean
	private SecurityService securityService;

	private AgendamentoRequest agendamentoRequest;

	private LocalDate data;

	private LocalTime horario;

	@BeforeEach
	public void setUp() {
		agendamentoRepository.deleteAll();

		ProcedimentoResponse procedimentoResponse = new ProcedimentoResponse(1L, "titulo", "descrição", BigDecimal.TEN,
				30, true, new CategoriaResponse(1L, "nome", true));
		when(procedimentoExternalService.procurarProcedimentoPorId(1L)).thenReturn(procedimentoResponse);
		data = LocalDate.now().plusDays(2);
		horario = LocalTime.of(2, 0);
		agendamentoRequest = new AgendamentoRequest(data, horario, procedimentoResponse.id());

		UsuarioAutenticadoDTO autenticadoDTO = new UsuarioAutenticadoDTO(1L, "usuario");
		when(securityService.getUsuarioAutenticado()).thenReturn(autenticadoDTO);
	}

	@Test
	@WithMockUser
	public void deveCriarAgendamentoComSucesso() throws JacksonException, Exception {
		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest))).andExpect(status().isCreated());

		List<Agendamento> agendamentosSalvos = agendamentoRepository.findAll();

		assertEquals(1, agendamentosSalvos.size());
	}

	@Test
	@WithMockUser
	public void naoDeveCriarAgendamentoQuandoExistirConflito() throws JacksonException, Exception {
		criarAgendamento();

		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest))).andExpect(status().isConflict());

		List<Agendamento> agendamentosSalvos = agendamentoRepository.findAll();

		assertEquals(1, agendamentosSalvos.size());
	}

	@Test
	@WithMockUser
	public void deveAtualizarAgendamentoComSucesso() throws JacksonException, Exception {
		Agendamento agendamento = criarAgendamento();

		ProcedimentoResponse procedimentoResponse = new ProcedimentoResponse(2L, "titulo diferente",
				"descrição diferente", BigDecimal.valueOf(100), 60, true,
				new CategoriaResponse(2L, "categoria diferente", true));

		when(procedimentoExternalService.procurarProcedimentoPorId(procedimentoResponse.id())).thenReturn(procedimentoResponse);

		agendamentoRequest = new AgendamentoRequest(LocalDate.now().plusDays(2), LocalTime.of(2, 0), procedimentoResponse.id());

		mockMvc.perform(put(BASE_URL + "/" + agendamento.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest)));
		
		agendamento = agendamentoRepository.findById(agendamento.getId()).get();
		
		assertEquals(agendamentoRequest.data(), agendamento.getData());
		assertEquals(agendamentoRequest.inicio(), agendamento.getInicio());
		assertEquals(agendamentoRequest.inicio().plusMinutes(procedimentoResponse.duracaoEmMinutos()), agendamento.getFim());
		assertEquals(procedimentoResponse.titulo(), agendamento.getTituloDoProcedimento());
		assertEquals(procedimentoResponse.preco(), agendamento.getPreco());
		assertEquals(procedimentoResponse.duracaoEmMinutos(), agendamento.getDuracaoEmMinutos());
		assertEquals(procedimentoResponse.categoria().nome(), agendamento.getNomeDaCategoria());
	}
	
	@Test
	@WithMockUser
	public void naoDeveAtualizarAgendamentoQuandoExistirConflito() throws JacksonException, Exception {
		Agendamento agendamento = criarAgendamento();
		
		Agendamento agendamentoParaAtualizacao = Agendamento.builder()
				.usuarioId(1L).nomeDoUsuario("usuario")
				.criadoEm(LocalDateTime.now())
				.inicio(LocalTime.of(3, 0))
				.fim(horario.plusMinutes(30))
				.data(data.plusDays(5))
				.tituloDoProcedimento("titulo")
				.preco(BigDecimal.TEN).duracaoEmMinutos(30)
				.nomeDaCategoria("categoria")
				.status(AgendamentoStatus.AGENDADO).build();
		
		agendamentoParaAtualizacao = agendamentoRepository.save(agendamentoParaAtualizacao);

		mockMvc.perform(put(BASE_URL + "/" + agendamentoParaAtualizacao.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(agendamentoRequest)))
		.andExpect(status().isConflict());
		
		assertNotEquals(agendamentoRequest.data(), agendamentoParaAtualizacao.getData());
		assertNotEquals(agendamentoRequest.inicio(), agendamentoParaAtualizacao.getInicio());
	}
	
	@Test
	@WithMockUser
	public void deveCancelarAgendamentoComSucesso() throws JacksonException, Exception {
		Agendamento agendamento = criarAgendamento();
		
		mockMvc.perform(patch(BASE_URL + "/" + agendamento.getId()+"/cancelar"))
		.andExpect(status().isNoContent());
		
		agendamento = agendamentoRepository.findById(agendamento.getId()).get();
		
		assertEquals(AgendamentoStatus.CANCELADO, agendamento.getStatus());
	}

	private Agendamento criarAgendamento() {
		Agendamento agendamento = Agendamento.builder()
				.usuarioId(1L).nomeDoUsuario("usuario")
				.criadoEm(LocalDateTime.now())
				.inicio(horario)
				.fim(horario.plusMinutes(30))
				.data(data)
				.tituloDoProcedimento("titulo")
				.preco(BigDecimal.TEN).duracaoEmMinutos(30)
				.nomeDaCategoria("categoria")
				.status(AgendamentoStatus.AGENDADO).build();

		return agendamentoRepository.save(agendamento);
	}
}
