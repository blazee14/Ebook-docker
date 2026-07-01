package com.api.capas.application.service.impl;

import com.api.capas.domain.dto.MetodoPagoDTO;
import com.api.capas.config.ResourceNotFoundException;
import com.api.capas.infrastructure.persistence.entities.MetodoPago;
import com.api.capas.infrastructure.persistence.repositories.MetodoPagoRepository;
import com.api.capas.application.service.MetodoPagoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MetodoPagoServiceImpl implements MetodoPagoService {

	private final MetodoPagoRepository metodoPagoRepository;

	public MetodoPagoServiceImpl(MetodoPagoRepository metodoPagoRepository) {
		this.metodoPagoRepository = metodoPagoRepository;
	}

	@Override
	public List<MetodoPago> getAllMetodosPago() {
		return metodoPagoRepository.findAll();
	}

	@Override
	public MetodoPago getMetodoPagoById(Integer id) {
		Integer metodoPagoId = Objects.requireNonNull(id, "id must not be null");
		return metodoPagoRepository.findById(metodoPagoId)
				.orElseThrow(() -> new ResourceNotFoundException("Método de pago no encontrado con ID: " + metodoPagoId));
	}

	@Override
	public MetodoPago saveMetodoPago(MetodoPagoDTO metodoPagoDTO) {
		MetodoPago metodoPago;

		if (metodoPagoDTO.getTipoMetodo() != MetodoPago.TipoMetodoPago.TARJETA) {
			throw new IllegalArgumentException("Solo se permite el tipo de método de pago 'TARJETA'.");
		}

		if (metodoPagoDTO.getId() != null) {
			Integer metodoPagoId = Objects.requireNonNull(metodoPagoDTO.getId(), "id must not be null");
			metodoPago = metodoPagoRepository.findById(metodoPagoId)
					.orElseThrow(() -> new ResourceNotFoundException(
							"Método de pago no encontrado con ID: " + metodoPagoId));

			if (!metodoPago.getNombre().equalsIgnoreCase(metodoPagoDTO.getNombre())) {
				Optional<MetodoPago> metodoConMismoNombre = metodoPagoRepository
						.findByNombreIgnoreCase(metodoPagoDTO.getNombre());
				if (metodoConMismoNombre.isPresent()
						&& !metodoConMismoNombre.get().getId().equals(metodoPagoId)) {
					throw new IllegalArgumentException(
							"Ya existe otro método de pago con el nombre: " + metodoPagoDTO.getNombre());
				}
			}
		} else {

			if (metodoPagoRepository.findByNombreIgnoreCase(metodoPagoDTO.getNombre()).isPresent()) {
				throw new IllegalArgumentException(
						"Ya existe un método de pago con el nombre: " + metodoPagoDTO.getNombre());
			}
			metodoPago = new MetodoPago();
		}

		metodoPago.setNombre(metodoPagoDTO.getNombre());
		metodoPago.setDescripcion(metodoPagoDTO.getDescripcion());
		metodoPago.setActivo(metodoPagoDTO.getActivo());
		metodoPago.setTipoMetodo(metodoPagoDTO.getTipoMetodo());

		return metodoPagoRepository.save(metodoPago);
	}

	@Override
	public void deleteMetodoPago(Integer id) {
		Integer metodoPagoId = Objects.requireNonNull(id, "id must not be null");
		if (!metodoPagoRepository.existsById(metodoPagoId)) {
			throw new ResourceNotFoundException("Método de pago no encontrado con ID: " + metodoPagoId);
		}
		metodoPagoRepository.deleteById(metodoPagoId);
	}
}