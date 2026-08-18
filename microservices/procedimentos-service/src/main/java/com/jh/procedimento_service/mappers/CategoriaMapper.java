package com.jh.procedimento_service.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.jh.procedimento_service.domain.Categoria;
import com.jh.procedimento_service.dto.CategoriaResponse;

@Mapper
public interface CategoriaMapper {
	
	CategoriaMapper INSTANCE = Mappers.getMapper(CategoriaMapper.class);
	
	CategoriaResponse entityToResponse(Categoria categoria);
	List<CategoriaResponse> listEntityToListDTO(List<Categoria> categorias);
}
