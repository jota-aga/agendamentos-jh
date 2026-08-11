package com.jh.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jh.auth_service.domain.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long>{
	Optional<UserRole> findByNome(String nome);
}
