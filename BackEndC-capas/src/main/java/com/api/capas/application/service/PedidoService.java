package com.api.capas.application.service;

import com.api.capas.domain.dto.ConfirmarCompraRequestDTO;
import com.api.capas.domain.dto.PedidoResponseDTO;
import com.api.capas.infrastructure.persistence.entities.Pedido;

import java.util.List;

public interface PedidoService {

    PedidoResponseDTO confirmarCompra(ConfirmarCompraRequestDTO request, Integer userId);

    List<PedidoResponseDTO> getAllPedidos();
    PedidoResponseDTO getPedidoById(Integer pedidoId);
    List<PedidoResponseDTO> getPedidosByUserId(Integer userId);
    PedidoResponseDTO actualizarEstadoPedido(Integer pedidoId, String nuevoEstado, Integer adminUserId);
    PedidoResponseDTO cancelarPedidoCliente(Integer pedidoId, Integer userId);
}