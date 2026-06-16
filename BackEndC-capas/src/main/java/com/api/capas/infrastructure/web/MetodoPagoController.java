package com.api.capas.infrastructure.web;

import com.api.capas.domain.dto.MetodoPagoDTO;
import com.api.capas.infrastructure.persistence.entities.MetodoPago;
import com.api.capas.application.service.MetodoPagoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metodos-pago")
@CrossOrigin(origins = "*")
public class MetodoPagoController {

    @Autowired
    private MetodoPagoService metodoPagoService;

    @GetMapping
    public ResponseEntity<List<MetodoPago>> getAllMetodosPago() {
        List<MetodoPago> metodosPago = metodoPagoService.getAllMetodosPago();
        return ResponseEntity.ok(metodosPago);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetodoPago> getMetodoPagoById(@PathVariable Integer id) {
        MetodoPago metodoPago = metodoPagoService.getMetodoPagoById(id);
        return ResponseEntity.ok(metodoPago);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MetodoPago> saveMetodoPago(@Valid @RequestBody MetodoPagoDTO metodoPagoDTO) {
        MetodoPago nuevoMetodoPago = metodoPagoService.saveMetodoPago(metodoPagoDTO);
        HttpStatus status = (metodoPagoDTO.getId() == null) ? HttpStatus.CREATED : HttpStatus.OK;
        return new ResponseEntity<>(nuevoMetodoPago, status);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMetodoPago(@PathVariable Integer id) {
        metodoPagoService.deleteMetodoPago(id);
        return ResponseEntity.noContent().build();
    }
}