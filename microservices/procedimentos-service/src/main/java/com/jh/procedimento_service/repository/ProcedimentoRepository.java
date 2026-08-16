package com.jh.procedimento_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jh.procedimento_service.domain.Categoria;
import com.jh.procedimento_service.domain.Procedimento;

public interface ProcedimentoRepository extends JpaRepository<Procedimento, Long> {
	List<Procedimento> findAllByAtivoTrue();
	List<Procedimento> findAllByCategoria(Categoria categoria);
}
