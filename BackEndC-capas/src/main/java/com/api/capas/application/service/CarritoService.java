package com.api.capas.application.service;

import com.api.capas.domain.dto.CarritoValidacionRequestDTO;
import com.api.capas.domain.dto.CarritoValidacionResponseDTO;

public interface CarritoService {

    CarritoValidacionResponseDTO validarCarrito(CarritoValidacionRequestDTO request);
}