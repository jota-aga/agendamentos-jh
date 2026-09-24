package com.jh.agendamento_service.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jh.agendamento_service.domain.Agendamento;
import com.jh.agendamento_service.dto.AgendamentoAdminRequest;
import com.jh.agendamento_service.dto.AgendamentoResponse;
import com.jh.agendamento_service.dto.AgendamentoStatusRequest;
import com.jh.agendamento_service.dto.ProcedimentoResponse;
import com.jh.agendamento_service.enums.AgendamentoStatus;
import com.jh.agendamento_service.exception.ConflitoDeHorarioException;
import com.jh.agendamento_service.exception.NaoEncontradoException;
import com.jh.agendamento_service.mapper.AgendamentoMapper;
import com.jh.agendamento_service.repository.AgendamentoCustomRepository;
import com.jh.agendamento_service.repository.AgendamentoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoAdminService {

	private final ProcedimentoExternalService procedimentoExternalService;

	private final AgendamentoRepository agendamentoRepository;

	private final AgendamentoCustomRepository agendamentoCustomRepository;

	public void criarAgendamento(AgendamentoAdminRequest agendamentoAdminRequest) {
		Agendamento agendamento = AgendamentoMapper.INSTANCE.requestToEntity(agendamentoAdminRequest);
		
		setarInformacoesDoProcedimento(agendamentoAdminRequest.procedimentoId(), agendamento);
		validarConflitoDeHorario(agendamento);

		agendamentoRepository.save(agendamento);
	}

	public void atualizarAgendamento(String id, AgendamentoAdminRequest agendamentoRequest) {
		Agendamento agendamento = procurarPorId(id);

		AgendamentoMapper.INSTANCE.updateEntity(agendamento, agendamentoRequest);

		setarInformacoesDoProcedimento(agendamentoRequest.procedimentoId(), agendamento);
		validarConflitoDeHorario(agendamento);

		agendamentoRepository.save(agendamento);
	}

	public void alterarStatusDoAgendamento(String id, AgendamentoStatusRequest statusRequest) {
		Agendamento agendamento = procurarPorId(id);

		agendamento.setStatus(statusRequest.status());

		agendamentoRepository.save(agendamento);
	}

	public List<AgendamentoResponse> listarAgendamentoFiltrados(Long usuarioId, LocalDate data, LocalTime inicio,
			LocalTime fim, String tituloDoProcedimento, AgendamentoStatus status, String sortBy) {
		
		List<Agendamento> agendamentos = agendamentoCustomRepository.procurarAgendamentoPorFiltros(usuarioId, data,
				inicio, fim, tituloDoProcedimento, status, sortBy);
		
		return AgendamentoMapper.INSTANCE.listEntityToListDTO(agendamentos);
	}

	private Agendamento procurarPorId(String id) {
		return agendamentoRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Agendamento"));
	}

	private void setarInformacoesDoProcedimento(Long procedimentoId, Agendamento agendamento) {
		ProcedimentoResponse procedimento = procedimentoExternalService.procurarProcedimentoPorId(procedimentoId);

		LocalTime fimDoProcedimento = agendamento.getInicio().plusMinutes(procedimento.duracaoEmMinutos());

		AgendamentoMapper.INSTANCE.setInformacoesDoProcedimento(agendamento, procedimento);
		agendamento.setFim(fimDoProcedimento);
	}

	private void validarConflitoDeHorario(Agendamento agendamento) {
		Boolean existeConflito = false;

		existeConflito = agendamentoCustomRepository.existeConflitoDeHorario(agendamento.getData(),
				agendamento.getInicio(), agendamento.getFim(), agendamento.getId());

		if (existeConflito)
			throw new ConflitoDeHorarioException();
	}
}
