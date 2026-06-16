package com.api.capas.domain.dto;

import java.math.BigDecimal;
import java.util.List;

public class CarritoValidacionResponseDTO {

    private List<ItemValidadoDTO> itemsValidados;
    private BigDecimal totalCalculado;
    private boolean carritoCompletoValido;

    public CarritoValidacionResponseDTO() {
    }

    public List<ItemValidadoDTO> getItemsValidados() {
        return itemsValidados;
    }

    public void setItemsValidados(List<ItemValidadoDTO> itemsValidados) {
        this.itemsValidados = itemsValidados;
    }

    public BigDecimal getTotalCalculado() {
        return totalCalculado;
    }

    public void setTotalCalculado(BigDecimal totalCalculado) {
        this.totalCalculado = totalCalculado;
    }

    public boolean isCarritoCompletoValido() {
        return carritoCompletoValido;
    }

    public void setCarritoCompletoValido(boolean carritoCompletoValido) {
        this.carritoCompletoValido = carritoCompletoValido;
    }
}