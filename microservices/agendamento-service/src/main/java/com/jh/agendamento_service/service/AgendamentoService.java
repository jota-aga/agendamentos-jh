package com.jh.agendamento_service.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.jh.agendamento_service.domain.Agendamento;
import com.jh.agendamento_service.dto.AgendamentoRequest;
import com.jh.agendamento_service.dto.ProcedimentoResponse;
import com.jh.agendamento_service.exception.ConflitoDeHorarioException;
import com.jh.agendamento_service.mapper.AgendamentoMapper;
import com.jh.agendamento_service.repository.AgendamentoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoService {
	
	private final ProcedimentoExternalService procedimentoExternalService;
	
	private final AgendamentoRepository agendamentoRepository;
	
	public void criarAgendamento(AgendamentoRequest agendamentoRequest) {
		Agendamento agendamento = AgendamentoMapper.INSTANCE.requestToEntity(agendamentoRequest);
		agendamento.setCriadoEm(LocalDateTime.now());
		
		setarInformacoesDoProcedimento(agendamentoRequest.procedimentoId(), agendamento);
		setarInformacoesDoUsuario(agendamento);
		validarHorario(agendamento);
		
		agendamentoRepository.save(agendamento);
	}
	
	private void setarInformacoesDoProcedimento(Long procedimentoId, Agendamento agendamento) {
		ProcedimentoResponse procedimento = procedimentoExternalService.procurarProcedimentoPorId(procedimentoId);
		LocalTime fimDoProcedimento = agendamento.getInicio().plusMinutes(procedimento.duracaoEmMinutos());

		agendamento.setTituloDoProcedimento(procedimento.titulo());
		agendamento.setPreco(procedimento.preco());
		agendamento.setDuracaoEmMinutos(procedimento.duracaoEmMinutos());
		agendamento.setNomeDaCategoria(procedimento.categoria().nome());
		agendamento.setFim(fimDoProcedimento);
	}
	
	private void validarHorario(Agendamento agendamento) {
		Boolean existeConflito = agendamentoRepository.existsByDataAndInicioLessThanAndFimGreaterThan(agendamento.getData(), 
				agendamento.getInicio(), agendamento.getFim());
		
		if(existeConflito) throw new ConflitoDeHorarioException();
	}
	
	private void setarInformacoesDoUsuario(Agendamento agendamento) {
		Authentication authentication = SecurityContextHolder.getContext()
			.getAuthentication();
		
		Jwt jwt = (Jwt) authentication.getPrincipal();
		
		Long usuarioId = Long.valueOf(jwt.getSubject());
		String nomeDoUsuario = jwt.getClaimAsString("nome");
		
		agendamento.setUsuarioId(usuarioId);
		agendamento.setNomeDoUsuario(nomeDoUsuario);
	}
}
