package com.jh.auth_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jh.auth_service.domain.Usuario;
import com.jh.auth_service.dto.UsuarioResponse;
import com.jh.auth_service.exceptions.NaoEncontradoException;
import com.jh.auth_service.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	
	public UsuarioResponse procurarUsuarioPorId(Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new NaoEncontradoException("usuario"));
		
		UsuarioResponse usuarioResponse = new UsuarioResponse(usuario.getEmail(), usuario.getNome());
		
		return usuarioResponse;
	}
	
	public UsuarioResponse procurarUsuarioPorEmail(String email) {
		Usuario usuario = usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new NaoEncontradoException("usuario"));
		
		UsuarioResponse usuarioResponse = new UsuarioResponse(usuario.getEmail(), usuario.getNome());
		
		return usuarioResponse;
	}
	
	public List<UsuarioResponse> procurarUsuarioPorNome(String nome) {
		List<Usuario> usuarios = usuarioRepository.findByNomeContainsIgnoreCase(nome);
		
		List<UsuarioResponse> usuariosResponse = usuarios.stream()
			.map(usuario -> new UsuarioResponse(usuario.getEmail(), usuario.getNome()))
			.toList();
		
		return usuariosResponse;
	}
}
