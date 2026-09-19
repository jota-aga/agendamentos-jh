package com.jh.agendamento_service.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.jh.agendamento_service.domain.Agendamento;
import com.jh.agendamento_service.enums.AgendamentoStatus;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AgendamentoCustomRepository {
	
	private final MongoTemplate mongoTemplate;
	
	public List<Agendamento> procurarAgendamentoPorFiltros(Long usuarioId, LocalDate data, LocalTime inicio,
			LocalTime fim, String tituloDoProcedimento, String sortBy, AgendamentoStatus status) {

		Query query = new Query();

		if (usuarioId != null) {
			query.addCriteria(Criteria.where("usuarioId").is(usuarioId));
		}

		if (data != null) {
			query.addCriteria(Criteria.where("data").is(data));
		}

		if (inicio != null) {
			query.addCriteria(Criteria.where("inicio").gte(inicio));
		}

		if (fim != null) {
			query.addCriteria(Criteria.where("fim").lte(fim));
		}

		if (tituloDoProcedimento != null) {
			query.addCriteria(Criteria.where("tituloDoProcedimento").regex(tituloDoProcedimento, "i"));
		}

		if (status != null) {
			query.addCriteria(Criteria.where("status").is(status));
		}
		
		if(sortBy == null)
			sortBy = "data";
		
		String campoOrdenacao = switch (sortBy) {
		case "data" -> "data";
		case "inicio" -> "inicio";
		case "criadoEm" -> "criadoEm";
		default -> "data";
		};

		query.with(Sort.by(Sort.Direction.DESC, campoOrdenacao));

		return mongoTemplate.find(query, Agendamento.class);
	}
}
