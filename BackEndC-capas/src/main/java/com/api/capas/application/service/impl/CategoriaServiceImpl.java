package com.api.capas.application.service.impl;

import com.api.capas.domain.dto.CategoriaDTO;
import com.api.capas.config.ResourceNotFoundException;
import com.api.capas.infrastructure.persistence.entities.Categoria;
import com.api.capas.infrastructure.persistence.repositories.CategoriaRepository;
import com.api.capas.application.service.CategoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.api.capas.infrastructure.persistence.repositories.ProductoRepository;

@Service
public class CategoriaServiceImpl implements CategoriaService {

	private final CategoriaRepository categoriaRepository;
	private final ProductoRepository productoRepository;

	public CategoriaServiceImpl(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
		this.categoriaRepository = categoriaRepository;
		this.productoRepository = productoRepository;
	}

	@Override
	public List<Categoria> getAllCategorias() {
		return categoriaRepository.findAll();
	}

	@Override
	public Categoria getCategoriaById(Integer id) {
		Integer categoriaId = Objects.requireNonNull(id, "id must not be null");
		return categoriaRepository.findById(categoriaId)
				.orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + categoriaId));
	}

	@Override
	public List<Categoria> getCategoriasByNombre(String nombre) {
		return categoriaRepository.findByNombreContainingIgnoreCase(nombre);
	}

	@Override
	public Categoria saveCategoria(CategoriaDTO categoriaDTO) {
		Objects.requireNonNull(categoriaDTO, "categoriaDTO must not be null");
		Categoria categoria;

		if (categoriaDTO.getId() != null) {
			Integer categoriaId = Objects.requireNonNull(categoriaDTO.getId(), "id must not be null");
			categoria = categoriaRepository.findById(categoriaId).orElseThrow(
					() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + categoriaId));

			if (!categoria.getNombre().equalsIgnoreCase(categoriaDTO.getNombre())) {
				Optional<Categoria> categoriaConMismoNombre = categoriaRepository
						.findByNombreIgnoreCase(categoriaDTO.getNombre());
				if (categoriaConMismoNombre.isPresent()
						&& !categoriaConMismoNombre.get().getId().equals(categoriaId)) {
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
		Integer categoriaId = Objects.requireNonNull(id, "id must not be null");
		Categoria categoria = categoriaRepository.findById(categoriaId)
				.orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + categoriaId));
		if (productoRepository.countByCategoriaId(categoriaId) > 0) {
			throw new IllegalArgumentException("No se puede eliminar la categoría '" + categoria.getNombre() + "' porque tiene libros asociados.");
			}
		categoriaRepository.deleteById(categoriaId);
		}
}