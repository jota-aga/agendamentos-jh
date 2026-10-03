package com.jh.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jh.auth_service.domain.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	Optional<Usuario> findByEmail(String email);

	Optional<Usuario> findByNomeContainsIgnoreCase(String nome);
}
