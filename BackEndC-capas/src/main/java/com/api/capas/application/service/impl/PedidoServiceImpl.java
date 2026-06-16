package com.api.capas.application.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.api.capas.domain.dto.ConfirmarCompraRequestDTO;
import com.api.capas.domain.dto.ItemCarritoDTO;
import com.api.capas.domain.dto.PedidoResponseDTO;
import com.api.capas.config.ResourceNotFoundException;
import com.api.capas.infrastructure.persistence.entities.*;
import com.api.capas.infrastructure.persistence.repositories.*;
import com.api.capas.application.service.PedidoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    @Autowired
    private HistorialPedidoRepository historialPedidoRepository;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    private PedidoResponseDTO convertToDto(Pedido pedido) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setIdUsuario(pedido.getUsuario().getId());
        dto.setNombreUsuario(pedido.getUsuario().getNombres() + " " + pedido.getUsuario().getApellidos());
        dto.setMetodoPagoNombre(pedido.getMetodoPago().getNombre());
        dto.setEstado(pedido.getEstado());
        dto.setTotal(pedido.getTotal());
        dto.setFecha(pedido.getFecha());
        dto.setCreatedAt(pedido.getCreatedAt());
        dto.setUpdatedAt(pedido.getUpdatedAt());
        dto.setClientSecret(pedido.getStripeClientSecret());
        dto.setPaymentIntentId(pedido.getStripePaymentIntentId());
        dto.setPaymentStatus(pedido.getStripePaymentStatus());

        dto.setDetalles(pedido.getDetalles().stream().map(detalle -> {
            PedidoResponseDTO.DetallePedidoResponseDTO detalleDto = new PedidoResponseDTO.DetallePedidoResponseDTO();
            detalleDto.setId(detalle.getId());
            detalleDto.setIdProducto(detalle.getProducto().getId());
            detalleDto.setTituloProducto(detalle.getProducto().getTitulo());
            detalleDto.setImagenProductoUrl(detalle.getProducto().getImagenUrl());
            detalleDto.setCantidad(detalle.getCantidad());
            detalleDto.setPrecioUnitario(detalle.getPrecioUnitario());
            detalleDto.setSubtotal(detalle.getSubtotal());
            return detalleDto;
        }).collect(Collectors.toList()));

        return dto;
    }

    @Override
    @Transactional
    public PedidoResponseDTO confirmarCompra(ConfirmarCompraRequestDTO request, Integer userId) {
        Stripe.apiKey = stripeSecretKey;

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        MetodoPago metodoPago = metodoPagoRepository.findById(request.getIdMetodoPago())
                .orElseThrow(() -> new ResourceNotFoundException("Método de pago no encontrado con ID: " + request.getIdMetodoPago()));

        if (!metodoPago.getActivo()) {
            throw new IllegalArgumentException("El método de pago seleccionado no está activo.");
        }

        BigDecimal totalCalculado = BigDecimal.ZERO;
        for (ItemCarritoDTO itemDto : request.getItems()) {
            Producto producto = productoRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + itemDto.getProductId()));
            if (producto.getStock() < itemDto.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getTitulo() + ". Disponible: " + producto.getStock());
            }
            totalCalculado = totalCalculado.add(producto.getPrecio().multiply(BigDecimal.valueOf(itemDto.getCantidad())));
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setMetodoPago(metodoPago);
        pedido.setEstado(Pedido.EstadoPedido.pendiente);
        pedido.setTotal(totalCalculado);

        if (metodoPago.getTipoMetodo() == MetodoPago.TipoMetodoPago.TARJETA) {
            if (request.getPaymentMethodId() == null || request.getPaymentMethodId().isEmpty()) {
                throw new IllegalArgumentException("PaymentMethod ID es obligatorio para pagos con tarjeta.");
            }
            try {
                long amountInCents = totalCalculado.multiply(new BigDecimal(100)).longValue();

                PaymentIntentCreateParams createParams =
                        PaymentIntentCreateParams.builder()
                                .setAmount(amountInCents)
                                .setCurrency("usd")
                                .setPaymentMethod(request.getPaymentMethodId())
                                .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                                .setConfirm(true)
                                .setReturnUrl("http://localhost:4200/confirmacion-pago")
                                .build();

                PaymentIntent paymentIntent = PaymentIntent.create(createParams);

                pedido.setStripePaymentIntentId(paymentIntent.getId());
                pedido.setStripeClientSecret(paymentIntent.getClientSecret());
                pedido.setStripePaymentStatus(paymentIntent.getStatus());

                if ("succeeded".equals(paymentIntent.getStatus())) {
                    pedido.setEstado(Pedido.EstadoPedido.procesado);
                } else if ("requires_action".equals(paymentIntent.getStatus()) || "requires_confirmation".equals(paymentIntent.getStatus())) {
                    pedido.setEstado(Pedido.EstadoPedido.pendiente);
                } else {
                    pedido.setEstado(Pedido.EstadoPedido.pago_fallido);
                    throw new IllegalArgumentException("El pago no pudo ser procesado. Estado: " + paymentIntent.getStatus());
                }

            } catch (StripeException e) {
                pedido.setEstado(Pedido.EstadoPedido.pago_fallido);
                Pedido savedFailedOrder = pedidoRepository.save(pedido);

                HistorialPedido historialFallo = new HistorialPedido();
                historialFallo.setPedido(savedFailedOrder);
                historialFallo.setUsuario(usuario);
                historialFallo.setAccion("Fallo de Pago");
                historialFallo.setDetallesAdicionales("Error al procesar el pago con Stripe: " + e.getMessage());
                historialPedidoRepository.save(historialFallo);

                throw new IllegalArgumentException("Error al procesar el pago: " + e.getMessage());
            }
        }

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        for (ItemCarritoDTO itemDto : request.getItems()) {
            Producto producto = productoRepository.findById(itemDto.getProductId()).get();

            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(itemDto.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(itemDto.getCantidad())));
            detalle.setPedido(pedidoGuardado);

            detallePedidoRepository.save(detalle);

            producto.setStock(producto.getStock() - itemDto.getCantidad());
            productoRepository.save(producto);
        }

        HistorialPedido historialInicial = new HistorialPedido();
        historialInicial.setPedido(pedidoGuardado);
        historialInicial.setUsuario(usuario);
        historialInicial.setAccion("Pedido Creado");
        historialInicial.setDetallesAdicionales("Pedido #" + pedidoGuardado.getId() + " creado con estado inicial: " + pedidoGuardado.getEstado().name());
        historialPedidoRepository.save(historialInicial);

        return convertToDto(pedidoGuardado);
    }

    @Override
    public List<PedidoResponseDTO> getAllPedidos() {
        return pedidoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PedidoResponseDTO getPedidoById(Integer pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + pedidoId));
        return convertToDto(pedido);
    }

    @Override
    public List<PedidoResponseDTO> getPedidosByUserId(Integer userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));
        return pedidoRepository.findByUsuario(usuario).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PedidoResponseDTO actualizarEstadoPedido(Integer pedidoId, String nuevoEstadoString, Integer adminUserId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + pedidoId));

        Usuario adminUsuario = usuarioRepository.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario administrador no encontrado con ID: " + adminUserId));

        Pedido.EstadoPedido nuevoEstado;
        try {
            nuevoEstado = Pedido.EstadoPedido.valueOf(nuevoEstadoString.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de pedido inválido: " + nuevoEstadoString);
        }

        HistorialPedido historial = new HistorialPedido();
        historial.setPedido(pedido);
        historial.setUsuario(adminUsuario);
        historial.setAccion("Estado Actualizado");
        historial.setDetallesAdicionales("Estado cambiado de '" + pedido.getEstado().name() + "' a '" + nuevoEstado.name() + "'");
        historialPedidoRepository.save(historial);

        pedido.setEstado(nuevoEstado);
        Pedido pedidoActualizado = pedidoRepository.save(pedido);

        return convertToDto(pedidoActualizado);
    }

    @Override
    @Transactional
    public PedidoResponseDTO cancelarPedidoCliente(Integer pedidoId, Integer userId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + pedidoId));

        if (!pedido.getUsuario().getId().equals(userId)) {
            throw new IllegalArgumentException("No tienes permiso para cancelar este pedido.");
        }

        if (pedido.getEstado() != Pedido.EstadoPedido.pendiente && pedido.getEstado() != Pedido.EstadoPedido.procesado) {
            throw new IllegalArgumentException("No se puede cancelar un pedido en estado '" + pedido.getEstado().name() + "'.");
        }

        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        }

        pedido.setEstado(Pedido.EstadoPedido.cancelado_cliente);
        Pedido pedidoActualizado = pedidoRepository.save(pedido);

        HistorialPedido historial = new HistorialPedido();
        historial.setPedido(pedidoActualizado);
        historial.setUsuario(pedido.getUsuario());
        historial.setAccion("Pedido Cancelado por Cliente");
        historial.setDetallesAdicionales("El pedido #" + pedidoActualizado.getId() + " fue cancelado por el cliente.");
        historialPedidoRepository.save(historial);

        return convertToDto(pedidoActualizado);
    }
}