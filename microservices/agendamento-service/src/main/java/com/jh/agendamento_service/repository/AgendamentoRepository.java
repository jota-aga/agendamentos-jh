package com.jh.agendamento_service.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.jh.agendamento_service.domain.Agendamento;

public interface AgendamentoRepository extends MongoRepository<Agendamento, String>{

	List<Agendamento> findAllByData(LocalDate data);
	
}
