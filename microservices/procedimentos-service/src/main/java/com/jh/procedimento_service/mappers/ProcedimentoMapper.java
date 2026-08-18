package com.jh.procedimento_service.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.jh.procedimento_service.domain.Procedimento;
import com.jh.procedimento_service.dto.ProcedimentoRequest;
import com.jh.procedimento_service.dto.ProcedimentoResponse;

@Mapper
public interface ProcedimentoMapper {
	
	ProcedimentoMapper INSTANCE = Mappers.getMapper(ProcedimentoMapper.class);
	
	Procedimento requestToEntity(ProcedimentoRequest dto);
	@Mapping(source = "categoria.nome", target = "categoriaNome")
	ProcedimentoResponse entityToResponse(Procedimento procedimento);
	List<ProcedimentoResponse> listEntityToListReponse(List<Procedimento> procedimentos);
}
