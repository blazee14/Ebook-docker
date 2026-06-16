package com.api.capas.infrastructure.web;

import com.api.capas.domain.dto.CarritoValidacionRequestDTO;
import com.api.capas.domain.dto.CarritoValidacionResponseDTO;
import com.api.capas.application.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "*")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    @PostMapping("/validar")
    public ResponseEntity<CarritoValidacionResponseDTO> validarCarrito(@Valid @RequestBody CarritoValidacionRequestDTO request) {
        CarritoValidacionResponseDTO response = carritoService.validarCarrito(request);
        return ResponseEntity.ok(response);
    }
}