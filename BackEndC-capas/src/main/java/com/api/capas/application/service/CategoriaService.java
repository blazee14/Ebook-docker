package com.api.capas.application.service;

import com.api.capas.domain.dto.CategoriaDTO;
import com.api.capas.infrastructure.persistence.entities.Categoria;

import java.util.List;

public interface CategoriaService {

    List<Categoria> getAllCategorias();
    Categoria getCategoriaById(Integer id);
    List<Categoria> getCategoriasByNombre(String nombre);

    Categoria saveCategoria(CategoriaDTO categoriaDTO);

    void deleteCategoria(Integer id);
}