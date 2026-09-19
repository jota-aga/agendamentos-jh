package com.jh.agendamento_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.jh.agendamento_service.domain.Agendamento;

public interface AgendamentoRepository extends MongoRepository<Agendamento, String>{
	
}
