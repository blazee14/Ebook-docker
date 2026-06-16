package com.api.capas.domain.dto;

import java.util.List;

import jakarta.validation.Valid;

public class CarritoValidacionRequestDTO {

	@Valid
    private List<ItemCarritoDTO> items;

    public CarritoValidacionRequestDTO() {
    }

    public List<ItemCarritoDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemCarritoDTO> items) {
        this.items = items;
    }
}