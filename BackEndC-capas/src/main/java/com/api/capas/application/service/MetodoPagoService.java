package com.api.capas.application.service;

import com.api.capas.domain.dto.MetodoPagoDTO;
import com.api.capas.infrastructure.persistence.entities.MetodoPago;

import java.util.List;

public interface MetodoPagoService {

    List<MetodoPago> getAllMetodosPago();
    MetodoPago getMetodoPagoById(Integer id);
    MetodoPago saveMetodoPago(MetodoPagoDTO metodoPagoDTO);

    void deleteMetodoPago(Integer id);
}