package com.jh.agendamento_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.jh.agendamento_service.domain.Agendamento;
import com.jh.agendamento_service.dto.AgendamentoRequest;
import com.jh.agendamento_service.dto.ProcedimentoResponse;

@Mapper
public interface AgendamentoMapper {

	AgendamentoMapper INSTANCE = Mappers.getMapper(AgendamentoMapper.class);

	Agendamento requestToEntity(AgendamentoRequest request);
	
	@Mapping(target="status", ignore = true)
	Agendamento updateEntityComoCliente(@MappingTarget Agendamento agendamento, AgendamentoRequest request);

	@Mapping(target = "tituloDoProcedimento", source = "procedimentoResponse.titulo")
	@Mapping(target = "preco", source = "procedimentoResponse.preco")
	@Mapping(target = "duracaoEmMinutos", source = "procedimentoResponse.duracaoEmMinutos")
	@Mapping(target = "nomeDaCategoria", source = "procedimentoResponse.categoria.nome")
	@Mapping(target = "fim", ignore = true)
	Agendamento setInformacoesDoProcedimento(@MappingTarget Agendamento agendamento,
			ProcedimentoResponse procedimentoResponse);
}
