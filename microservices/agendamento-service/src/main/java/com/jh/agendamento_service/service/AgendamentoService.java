package com.jh.agendamento_service.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jh.agendamento_service.domain.Agendamento;
import com.jh.agendamento_service.dto.AgendamentoRequest;
import com.jh.agendamento_service.dto.AgendamentoResponse;
import com.jh.agendamento_service.dto.AgendamentoStatusRequest;
import com.jh.agendamento_service.dto.ProcedimentoResponse;
import com.jh.agendamento_service.dto.UsuarioAutenticadoDTO;
import com.jh.agendamento_service.enums.AgendamentoStatus;
import com.jh.agendamento_service.exception.ConflitoDeHorarioException;
import com.jh.agendamento_service.exception.ConflitoDeOperacaoException;
import com.jh.agendamento_service.exception.NaoAutorizadoException;
import com.jh.agendamento_service.exception.NaoEncontradoException;
import com.jh.agendamento_service.exception.ProcedimentoNaoDisponivelException;
import com.jh.agendamento_service.mapper.AgendamentoMapper;
import com.jh.agendamento_service.repository.AgendamentoCustomRepository;
import com.jh.agendamento_service.repository.AgendamentoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoService {
	private final SecurityService securityService;

	private final ProcedimentoExternalService procedimentoExternalService;

	private final AgendamentoRepository agendamentoRepository;

	private final AgendamentoCustomRepository agendamentoCustomRepository;

	public void criarAgendamento(AgendamentoRequest agendamentoRequest) {
		Agendamento agendamento = AgendamentoMapper.INSTANCE.requestToEntity(agendamentoRequest);

		agendamento.setCriadoEm(LocalDateTime.now());
		agendamento.setStatus(AgendamentoStatus.AGENDADO);

		setarInformacoesDoUsuario(agendamento);
		setarInformacoesDoProcedimento(agendamentoRequest.procedimentoId(), agendamento);

		validarConflitoDeHorario(agendamento);
		agendamentoRepository.save(agendamento);
	}

	public void atualizarAgendamentoComoCliente(String id, AgendamentoRequest agendamentoRequest) {
		Agendamento agendamento = procurarPorId(id);
		UsuarioAutenticadoDTO usuarioAutenticado = securityService.getUsuarioAutenticado();

		if (!agendamento.getUsuarioId().equals(usuarioAutenticado.id()))
			throw new NaoAutorizadoException("Esse agendamento não lhe pertence");

		if (agendamento.getStatus() != AgendamentoStatus.AGENDADO)
			throw new ConflitoDeOperacaoException("Não é possível atualizar o agendamento");

		AgendamentoMapper.INSTANCE.updateEntity(agendamento, agendamentoRequest);

		setarInformacoesDoProcedimento(agendamentoRequest.procedimentoId(), agendamento);
		validarConflitoDeHorario(agendamento);
		validarAntecedenciaMinima(agendamento);

		agendamentoRepository.save(agendamento);
	}
	
	public void atualizarAgendamentoComoAdmin(String id, AgendamentoRequest agendamentoRequest) {
		Agendamento agendamento = procurarPorId(id);

		AgendamentoMapper.INSTANCE.updateEntity(agendamento, agendamentoRequest);

		setarInformacoesDoProcedimento(agendamentoRequest.procedimentoId(), agendamento);
		validarConflitoDeHorario(agendamento);

		agendamentoRepository.save(agendamento);
	}

	public List<LocalTime> horariosDisponiveis(LocalTime inicioDoExpediente, LocalTime fimDoExpediente, LocalDate data,
			Long procedimentoId) {

		List<Agendamento> agendamentoJaMarcados = agendamentoRepository.findAllByData(data);

		ProcedimentoResponse procedimentoResponse = procedimentoExternalService
				.procurarProcedimentoPorId(procedimentoId);

		List<LocalTime> horariosDisponiveis = new ArrayList<>();

		LocalTime horario = inicioDoExpediente;

		Integer duracaoEmMinutosDoProcedimento = procedimentoResponse.duracaoEmMinutos();

		while (!horario.plusMinutes(duracaoEmMinutosDoProcedimento).isAfter(fimDoExpediente)) {
			
			LocalTime horarioInicial = horario;
			LocalTime horarioFim = horarioInicial.plusMinutes(duracaoEmMinutosDoProcedimento);

			boolean possuiConflito = agendamentoJaMarcados.stream()
					.anyMatch(agendamento -> agendamento.getInicio().isBefore(horarioFim)
							&& agendamento.getFim().isAfter(horarioInicial));

			if (!possuiConflito) {
				horariosDisponiveis.add(horarioInicial);
			}

			horario = horario.plusMinutes(duracaoEmMinutosDoProcedimento);
		}

		return horariosDisponiveis;
	}
	
	public void alterarStatusDoAgendamento(String id, AgendamentoStatusRequest statusRequest) {
		Agendamento agendamento = procurarPorId(id);
		
		agendamento.setStatus(statusRequest.status());
		
		agendamentoRepository.save(agendamento);
	}
	
	public AgendamentoResponse procurarAgendamentoPorId(String id) {
		Agendamento agendamento = procurarPorId(id);
		
		return AgendamentoMapper.INSTANCE.entityToResponse(agendamento);
	}
	
	private Agendamento procurarPorId(String id) {
		return agendamentoRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Agendamento"));
	}

	private void setarInformacoesDoProcedimento(Long procedimentoId, Agendamento agendamento) {
		ProcedimentoResponse procedimento = procedimentoExternalService.procurarProcedimentoPorId(procedimentoId);

		if (!procedimento.ativo()) {
			throw new ProcedimentoNaoDisponivelException();
		}

		LocalTime fimDoProcedimento = agendamento.getInicio().plusMinutes(procedimento.duracaoEmMinutos());

		AgendamentoMapper.INSTANCE.setInformacoesDoProcedimento(agendamento, procedimento);
		agendamento.setFim(fimDoProcedimento);
	}

	private void validarConflitoDeHorario(Agendamento agendamento) {
		Boolean existeConflito = false;

		existeConflito = agendamentoCustomRepository.existeConflitoDeHorario(agendamento.getData(),
				agendamento.getInicio(), agendamento.getFim(), AgendamentoStatus.CANCELADO, agendamento.getId());

		if (existeConflito)
			throw new ConflitoDeHorarioException();
	}

	private void setarInformacoesDoUsuario(Agendamento agendamento) {
		UsuarioAutenticadoDTO usuarioAutenticado = securityService.getUsuarioAutenticado();

		agendamento.setUsuarioId(usuarioAutenticado.id());
		agendamento.setNomeDoUsuario(usuarioAutenticado.nome());
	}

	private void validarAntecedenciaMinima(Agendamento agendamento) {
		LocalDateTime agora = LocalDateTime.now();
		LocalDateTime dataHoraDoAgendamento = LocalDateTime.of(agendamento.getData(), agendamento.getInicio());

		Duration duracao = Duration.between(agora, dataHoraDoAgendamento);

		if (duracao.toHours() < 12) {
			throw new ConflitoDeOperacaoException(
					"Não é possível finalizar a operação, pois faltam menos de 12 horas para o agendamento");
		}
	}
}
