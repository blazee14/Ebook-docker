package com.api.capas.infrastructure.web;

import com.api.capas.domain.dto.ConfirmarCompraRequestDTO;
import com.api.capas.domain.dto.PedidoResponseDTO;
import com.api.capas.infrastructure.persistence.entities.Usuario;
import com.api.capas.application.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    private Integer getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Usuario no autenticado.");
        }

        if (authentication.getPrincipal() instanceof Usuario) {
            return ((Usuario) authentication.getPrincipal()).getId();
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            String email = ((UserDetails) authentication.getPrincipal()).getUsername();
            throw new IllegalStateException("El principal de seguridad no es una instancia de Usuario, no se pudo obtener el ID. Email: " + email);
        }
        throw new IllegalStateException("No se pudo obtener el ID del usuario autenticado.");
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    @PostMapping("/confirmar")
    public ResponseEntity<PedidoResponseDTO> confirmarCompra(@Valid @RequestBody ConfirmarCompraRequestDTO request) {
        Integer userId = getAuthenticatedUserId();
        PedidoResponseDTO nuevoPedido = pedidoService.confirmarCompra(request, userId);
        return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> getAllPedidos() {
        List<PedidoResponseDTO> pedidos = pedidoService.getAllPedidos();
        return ResponseEntity.ok(pedidos);
    }

    @PreAuthorize("hasAnyRole('ADMIN') or @pedidoServiceImpl.getPedidoById(#pedidoId).getIdUsuario() == authentication.principal.id")
    @GetMapping("/{pedidoId}")
    public ResponseEntity<PedidoResponseDTO> getPedidoById(@PathVariable Integer pedidoId) {
        PedidoResponseDTO pedido = pedidoService.getPedidoById(pedidoId);
        return ResponseEntity.ok(pedido);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.getId()")
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<PedidoResponseDTO>> getPedidosByUserId(@PathVariable Integer userId) {
        List<PedidoResponseDTO> pedidos = pedidoService.getPedidosByUserId(userId);
        return ResponseEntity.ok(pedidos);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{pedidoId}/estado")
    public ResponseEntity<PedidoResponseDTO> actualizarEstadoPedido(@PathVariable Integer pedidoId, @RequestParam String nuevoEstadoString) {
        Integer adminUserId = getAuthenticatedUserId();
        PedidoResponseDTO pedidoActualizado = pedidoService.actualizarEstadoPedido(pedidoId, nuevoEstadoString, adminUserId);
        return ResponseEntity.ok(pedidoActualizado);
    }

    @PreAuthorize("hasRole('CLIENTE') and @pedidoServiceImpl.getPedidoById(#pedidoId).getIdUsuario() == authentication.principal.id")
    @PutMapping("/{pedidoId}/cancelar")
    public ResponseEntity<PedidoResponseDTO> cancelarPedidoCliente(@PathVariable Integer pedidoId) {
        Integer userId = getAuthenticatedUserId();
        PedidoResponseDTO pedidoCancelado = pedidoService.cancelarPedidoCliente(pedidoId, userId);
        return ResponseEntity.ok(pedidoCancelado);
    }
}