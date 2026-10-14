package com.jh.procedimento_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jh.procedimento_service.domain.Categoria;
import com.jh.procedimento_service.domain.Procedimento;

public interface ProcedimentoRepository extends JpaRepository<Procedimento, Long> {
	Optional<Procedimento> findByIdAndAtivoTrue(Long id);
	List<Procedimento> findAllByAtivoTrue();
	List<Procedimento> findAllByCategoria(Categoria categoria);
}
