package com.api.capas.domain.dto;

import com.api.capas.infrastructure.persistence.entities.MetodoPago;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MetodoPagoDTO {

    private Integer id; // <-- ¡NUEVO CAMPO PARA UNIFICACIÓN!

    @NotBlank(message = "El nombre del método de pago es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede exceder los 1000 caracteres")
    private String descripcion;

    @NotNull(message = "El estado 'activo' es obligatorio")
    private Boolean activo;

    @NotNull(message = "El tipo de método de pago es obligatorio")
    private MetodoPago.TipoMetodoPago tipoMetodo;

    public MetodoPagoDTO() {
    }

    public Integer getId() { 
        return id;
    }

    public void setId(Integer id) { 
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public MetodoPago.TipoMetodoPago getTipoMetodo() {
        return tipoMetodo;
    }

    public void setTipoMetodo(MetodoPago.TipoMetodoPago tipoMetodo) {
        this.tipoMetodo = tipoMetodo;
    }
}