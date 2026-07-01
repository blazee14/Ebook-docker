package com.api.capas.infrastructure.persistence.repositories;

import com.api.capas.infrastructure.persistence.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    Optional<Producto> findByIsbn(String isbn);

    List<Producto> findByTituloContainingIgnoreCase(String titulo);

    List<Producto> findByAutorContainingIgnoreCase(String autor);

    List<Producto> findByCategoria_Id(Integer categoriaId);

    List<Producto> findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCase(String titulo, String autor);
    
    long countByCategoriaId(Integer categoriaId); 
}