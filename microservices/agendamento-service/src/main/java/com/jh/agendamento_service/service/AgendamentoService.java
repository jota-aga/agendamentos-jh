package com.jh.agendamento_service.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Service;

import com.jh.agendamento_service.domain.Agendamento;
import com.jh.agendamento_service.dto.AgendamentoRequest;
import com.jh.agendamento_service.dto.ProcedimentoResponse;
import com.jh.agendamento_service.dto.UsuarioAutenticado;
import com.jh.agendamento_service.exception.ConflitoDeHorarioException;
import com.jh.agendamento_service.exception.NaoAutorizadoException;
import com.jh.agendamento_service.exception.NaoEncontradoException;
import com.jh.agendamento_service.exception.ProcedimentoNaoDisponivelException;
import com.jh.agendamento_service.mapper.AgendamentoMapper;
import com.jh.agendamento_service.repository.AgendamentoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoService {
	private final SecurityService securityService;

	private final ProcedimentoExternalService procedimentoExternalService;

	private final AgendamentoRepository agendamentoRepository;

	public void criarAgendamento(AgendamentoRequest agendamentoRequest) {
		Agendamento agendamento = AgendamentoMapper.INSTANCE.requestToEntity(agendamentoRequest);
		agendamento.setCriadoEm(LocalDateTime.now());
		
		setarInformacoesDoUsuario(agendamento);
		setarInformacoesDoProcedimento(agendamentoRequest.procedimentoId(), agendamento);
		validarHorario(agendamento);

		agendamentoRepository.save(agendamento);
	}

	public void atualizarAgendamentoComoCliente(Long id, AgendamentoRequest agendamentoRequest) {
		Agendamento agendamento = procurarPorId(id);
		UsuarioAutenticado usuarioAutenticado = securityService.getUsuarioAutenticado();

		if (!agendamento.getUsuarioId().equals(usuarioAutenticado.id()))
			throw new NaoAutorizadoException("Esse agendamento não lhe pertence");

		agendamento = AgendamentoMapper.INSTANCE.updateEntity(agendamento, agendamentoRequest);

		setarInformacoesDoProcedimento(agendamentoRequest.procedimentoId(), agendamento);
		validarHorario(agendamento);

		agendamentoRepository.save(agendamento);
	}

	public Agendamento procurarPorId(Long id) {
		return agendamentoRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Agendamento"));
	}

	private void setarInformacoesDoProcedimento(Long procedimentoId, Agendamento agendamento) {
		ProcedimentoResponse procedimento = procedimentoExternalService.procurarProcedimentoPorId(procedimentoId);

		if (!procedimento.ativo()) {
			throw new ProcedimentoNaoDisponivelException();
		}

		LocalTime fimDoProcedimento = agendamento.getInicio().plusMinutes(procedimento.duracaoEmMinutos());

		agendamento.setTituloDoProcedimento(procedimento.titulo());
		agendamento.setPreco(procedimento.preco());
		agendamento.setDuracaoEmMinutos(procedimento.duracaoEmMinutos());
		agendamento.setNomeDaCategoria(procedimento.categoria().nome());
		agendamento.setFim(fimDoProcedimento);
	}

	private void validarHorario(Agendamento agendamento) {
		Boolean existeConflito = false;

		if (agendamento.getId() == null) {
			existeConflito = agendamentoRepository.existsByDataAndInicioLessThanAndFimGreaterThan(agendamento.getData(),
					agendamento.getInicio(), agendamento.getFim());
		} else {
			existeConflito = agendamentoRepository.existsByDataAndInicioLessThanAndFimGreaterThanAndIdNot(
					agendamento.getData(), agendamento.getInicio(), agendamento.getFim(), agendamento.getId());
		}

		if (existeConflito)
			throw new ConflitoDeHorarioException();
	}

	private void setarInformacoesDoUsuario(Agendamento agendamento) {
		UsuarioAutenticado usuarioAutenticado = securityService.getUsuarioAutenticado();

		agendamento.setUsuarioId(usuarioAutenticado.id());
		agendamento.setNomeDoUsuario(usuarioAutenticado.nome());
	}
}
