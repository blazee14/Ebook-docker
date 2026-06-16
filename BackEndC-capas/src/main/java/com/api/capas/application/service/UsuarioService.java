package com.api.capas.application.service;

import org.springframework.stereotype.Service;

import com.api.capas.domain.dto.RegistroDTO;
import com.api.capas.infrastructure.persistence.entities.Usuario;

@Service
public interface UsuarioService {
    Usuario registrar(RegistroDTO dto);
    Usuario buscarPorEmail(String email);
    Usuario validarCredenciales(String email, String password); 
}