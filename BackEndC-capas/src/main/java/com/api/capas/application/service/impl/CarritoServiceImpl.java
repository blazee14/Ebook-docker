package com.api.capas.application.service.impl;

import com.api.capas.domain.dto.CarritoValidacionRequestDTO;
import com.api.capas.domain.dto.CarritoValidacionResponseDTO;
import com.api.capas.domain.dto.ItemCarritoDTO;
import com.api.capas.domain.dto.ItemValidadoDTO;
import com.api.capas.infrastructure.persistence.entities.Producto;
import com.api.capas.infrastructure.persistence.repositories.ProductoRepository;
import com.api.capas.application.service.CarritoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CarritoServiceImpl implements CarritoService {

    private final ProductoRepository productoRepository;

    public CarritoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public CarritoValidacionResponseDTO validarCarrito(CarritoValidacionRequestDTO request) {
        Objects.requireNonNull(request, "request must not be null");
        List<ItemValidadoDTO> itemsValidados = new ArrayList<>();
        BigDecimal totalCalculado = BigDecimal.ZERO;
        boolean carritoCompletoValido = true;

        for (ItemCarritoDTO itemDto : request.getItems()) {
            ItemValidadoDTO itemValidado = new ItemValidadoDTO();
            itemValidado.setProductId(itemDto.getProductId());
            itemValidado.setCantidadSolicitada(itemDto.getCantidad());
            itemValidado.setValido(true);

            Integer productId = Objects.requireNonNull(itemDto.getProductId(), "productId must not be null");
            Optional<Producto> productoOptional = productoRepository.findById(productId);

            if (productoOptional.isPresent()) {
                Producto producto = productoOptional.get();
                itemValidado.setTitulo(producto.getTitulo());
                itemValidado.setImagenUrl(producto.getImagenUrl());
                itemValidado.setPrecioUnitario(producto.getPrecio());
                itemValidado.setCantidadDisponible(producto.getStock());

                if (itemDto.getCantidad() > producto.getStock()) {
                    itemValidado.setValido(false);
                    itemValidado.setMensaje("Cantidad solicitada excede el stock disponible. Solo hay " + producto.getStock() + " unidades.");
                    carritoCompletoValido = false;
                } else if (itemDto.getCantidad() <= 0) {
                    itemValidado.setValido(false);
                    itemValidado.setMensaje("La cantidad debe ser mayor que cero.");
                    carritoCompletoValido = false;
                } else {
                    totalCalculado = totalCalculado.add(producto.getPrecio().multiply(BigDecimal.valueOf(itemDto.getCantidad())));
                }
            } else {
                itemValidado.setValido(false);
                itemValidado.setMensaje("Producto no encontrado.");
                itemValidado.setCantidadDisponible(0);
                itemValidado.setPrecioUnitario(BigDecimal.ZERO);
                carritoCompletoValido = false;
            }
            itemsValidados.add(itemValidado);
        }

        CarritoValidacionResponseDTO response = new CarritoValidacionResponseDTO();
        response.setItemsValidados(itemsValidados);
        response.setTotalCalculado(totalCalculado);
        response.setCarritoCompletoValido(carritoCompletoValido);

        return response;
    }
}