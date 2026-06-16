package com.api.capas.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ConfirmarCompraRequestDTO {

    @NotNull(message = "El ID del método de pago es obligatorio")
    @Min(value = 1, message = "El ID del método de pago debe ser válido")
    private Integer idMetodoPago;

    @NotEmpty(message = "La lista de ítems del carrito no puede estar vacía")
    @Valid
    private List<ItemCarritoDTO> items;

    private String paymentMethodId;

    public ConfirmarCompraRequestDTO() {
    }

    public Integer getIdMetodoPago() {
        return idMetodoPago;
    }

    public void setIdMetodoPago(Integer idMetodoPago) {
        this.idMetodoPago = idMetodoPago;
    }

    public List<ItemCarritoDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemCarritoDTO> items) {
        this.items = items;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
}