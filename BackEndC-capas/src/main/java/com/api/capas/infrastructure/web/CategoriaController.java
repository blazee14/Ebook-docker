package com.api.capas.infrastructure.web;

import com.api.capas.domain.dto.CategoriaDTO;
import com.api.capas.infrastructure.persistence.entities.Categoria;
import com.api.capas.application.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<Categoria>> getAllCategorias() {
        List<Categoria> categorias = categoriaService.getAllCategorias();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getCategoriaById(@PathVariable Integer id) {
        Categoria categoria = categoriaService.getCategoriaById(id);
        return ResponseEntity.ok(categoria);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Categoria>> getCategoriasByNombre(@RequestParam String nombre) {
        List<Categoria> categorias = categoriaService.getCategoriasByNombre(nombre);
        return ResponseEntity.ok(categorias);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Categoria> saveCategoria(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        Categoria result = categoriaService.saveCategoria(categoriaDTO);
        HttpStatus status = (categoriaDTO.getId() == null) ? HttpStatus.CREATED : HttpStatus.OK;
        return new ResponseEntity<>(result, status);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Integer id) {
        categoriaService.deleteCategoria(id);
        return ResponseEntity.noContent().build();
    }
}