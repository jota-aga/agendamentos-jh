package com.jh.procedimento_service.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.jh.procedimento_service.domain.Procedimento;
import com.jh.procedimento_service.dto.procedimento.ProcedimentoRequest;
import com.jh.procedimento_service.dto.procedimento.ProcedimentoResponse;

@Mapper(componentModel = "spring")
public interface ProcedimentoMapper {
	
	ProcedimentoMapper INSTANCE = Mappers.getMapper(ProcedimentoMapper.class);
	
	Procedimento requestToEntity(ProcedimentoRequest dto);
	ProcedimentoResponse entityToResponse(Procedimento procedimento);
	List<ProcedimentoResponse> listEntityToListReponse(List<Procedimento> procedimentos);
}
