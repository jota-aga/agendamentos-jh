package com.jh.agendamento_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.jh.agendamento_service.domain.Agendamento;
import com.jh.agendamento_service.dto.AgendamentoRequest;
@Mapper
public interface AgendamentoMapper {
	
	AgendamentoMapper INSTANCE = Mappers.getMapper(AgendamentoMapper.class);
	
	Agendamento requestToEntity(AgendamentoRequest request);
	Agendamento updateEntity(@MappingTarget Agendamento agendamento, AgendamentoRequest request);
}
