package com.api.capas.application.service.impl;

import com.api.capas.domain.dto.MetodoPagoDTO;
import com.api.capas.config.ResourceNotFoundException;
import com.api.capas.infrastructure.persistence.entities.MetodoPago;
import com.api.capas.infrastructure.persistence.repositories.MetodoPagoRepository;
import com.api.capas.application.service.MetodoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MetodoPagoServiceImpl implements MetodoPagoService {

	@Autowired
	private MetodoPagoRepository metodoPagoRepository;

	@Override
	public List<MetodoPago> getAllMetodosPago() {
		return metodoPagoRepository.findAll();
	}

	@Override
	public MetodoPago getMetodoPagoById(Integer id) {
		return metodoPagoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Método de pago no encontrado con ID: " + id));
	}

	@Override
	public MetodoPago saveMetodoPago(MetodoPagoDTO metodoPagoDTO) {
		MetodoPago metodoPago;

		if (metodoPagoDTO.getTipoMetodo() != MetodoPago.TipoMetodoPago.TARJETA) {
			throw new IllegalArgumentException("Solo se permite el tipo de método de pago 'TARJETA'.");
		}

		if (metodoPagoDTO.getId() != null) {
			metodoPago = metodoPagoRepository.findById(metodoPagoDTO.getId())
					.orElseThrow(() -> new ResourceNotFoundException(
							"Método de pago no encontrado con ID: " + metodoPagoDTO.getId()));

			if (!metodoPago.getNombre().equalsIgnoreCase(metodoPagoDTO.getNombre())) {
				Optional<MetodoPago> metodoConMismoNombre = metodoPagoRepository
						.findByNombreIgnoreCase(metodoPagoDTO.getNombre());
				if (metodoConMismoNombre.isPresent()
						&& !metodoConMismoNombre.get().getId().equals(metodoPagoDTO.getId())) {
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
		if (!metodoPagoRepository.existsById(id)) {
			throw new ResourceNotFoundException("Método de pago no encontrado con ID: " + id);
		}
		metodoPagoRepository.deleteById(id);
	}
}