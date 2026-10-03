package com.jh.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jh.auth_service.domain.UsuarioRole;

public interface UsuarioRoleRepository extends JpaRepository<UsuarioRole, Long>{
	Optional<UsuarioRole> findByNome(String nome);
}
