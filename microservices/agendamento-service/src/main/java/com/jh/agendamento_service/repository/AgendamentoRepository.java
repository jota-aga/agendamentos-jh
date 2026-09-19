package com.jh.agendamento_service.repository;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.jh.agendamento_service.domain.Agendamento;

public interface AgendamentoRepository extends MongoRepository<Agendamento, String>{
	
	Boolean existsByDataAndInicioLessThanAndFimGreaterThanAndStatusIsNotCANCELADO(LocalDate data, LocalTime inicio, LocalTime fim); 
	Boolean existsByDataAndInicioLessThanAndFimGreaterThanAndIdNotAndStatusIsNotCANCELADO(LocalDate data, LocalTime inicio, LocalTime fim, String id);
}
