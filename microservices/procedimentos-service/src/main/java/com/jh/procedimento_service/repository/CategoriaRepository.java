package com.jh.procedimento_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jh.procedimento_service.domain.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>{

}
