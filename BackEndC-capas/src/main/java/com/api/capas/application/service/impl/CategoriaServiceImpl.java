package com.api.capas.application.service.impl;

import com.api.capas.domain.dto.CategoriaDTO;
import com.api.capas.config.ResourceNotFoundException;
import com.api.capas.infrastructure.persistence.entities.Categoria;
import com.api.capas.infrastructure.persistence.repositories.CategoriaRepository;
import com.api.capas.application.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import com.api.capas.infrastructure.persistence.repositories.ProductoRepository;

@Service
public class CategoriaServiceImpl implements CategoriaService {

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private ProductoRepository productoRepository;

	@Override
	public List<Categoria> getAllCategorias() {
		return categoriaRepository.findAll();
	}

	@Override
	public Categoria getCategoriaById(Integer id) {
		return categoriaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
	}

	@Override
	public List<Categoria> getCategoriasByNombre(String nombre) {
		return categoriaRepository.findByNombreContainingIgnoreCase(nombre);
	}

	@Override
	public Categoria saveCategoria(CategoriaDTO categoriaDTO) {
		Categoria categoria;

		if (categoriaDTO.getId() != null) {
			categoria = categoriaRepository.findById(categoriaDTO.getId()).orElseThrow(
					() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + categoriaDTO.getId()));

			if (!categoria.getNombre().equalsIgnoreCase(categoriaDTO.getNombre())) {
				Optional<Categoria> categoriaConMismoNombre = categoriaRepository
						.findByNombreIgnoreCase(categoriaDTO.getNombre());
				if (categoriaConMismoNombre.isPresent()
						&& !categoriaConMismoNombre.get().getId().equals(categoriaDTO.getId())) {
					throw new IllegalArgumentException(
							"Ya existe otra categoría con el nombre: " + categoriaDTO.getNombre());
				}
			}
		} else {
			if (categoriaRepository.findByNombreIgnoreCase(categoriaDTO.getNombre()).isPresent()) {
				throw new IllegalArgumentException(
						"Ya existe una categoría con el nombre: " + categoriaDTO.getNombre());
			}
			categoria = new Categoria();
		}

		categoria.setNombre(categoriaDTO.getNombre());
		categoria.setDescripcion(categoriaDTO.getDescripcion());

		return categoriaRepository.save(categoria);
	}

	@Override
    @Transactional
    public void deleteCategoria(Integer id) {
		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
		if (productoRepository.countByCategoriaId(id) > 0) {
			throw new IllegalArgumentException("No se puede eliminar la categoría '" + categoria.getNombre() + "' porque tiene libros asociados.");
			}
		categoriaRepository.deleteById(id);
		}
}