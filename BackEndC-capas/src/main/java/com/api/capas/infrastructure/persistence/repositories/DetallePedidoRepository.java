package com.api.capas.infrastructure.persistence.repositories;

import com.api.capas.domain.dto.ProductoMasVendidoDTO;
import com.api.capas.infrastructure.persistence.entities.DetallePedido;
import com.api.capas.infrastructure.persistence.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {

    List<DetallePedido> findByPedido(Pedido pedido);

    @Query("SELECT new com.api.capas.domain.dto.ProductoMasVendidoDTO("
            + "dp.producto.id, dp.producto.isbn, dp.producto.titulo, dp.producto.autor, dp.producto.imagenUrl, dp.producto.precio, SUM(dp.cantidad)) "
            + "FROM DetallePedido dp "
            + "GROUP BY dp.producto.id, dp.producto.isbn, dp.producto.titulo, dp.producto.autor, dp.producto.imagenUrl, dp.producto.precio "
            + "ORDER BY SUM(dp.cantidad) DESC")
    List<ProductoMasVendidoDTO> findTopSellingProducts();

    @Query(value = "SELECT new com.api.capas.domain.dto.ProductoMasVendidoDTO("
            + "dp.producto.id, dp.producto.isbn, dp.producto.titulo, dp.producto.autor, dp.producto.imagenUrl, dp.producto.precio, SUM(dp.cantidad)) "
            + "FROM DetallePedido dp "
            + "GROUP BY dp.producto.id, dp.producto.isbn, dp.producto.titulo, dp.producto.autor, dp.producto.imagenUrl, dp.producto.precio "
            + "ORDER BY SUM(dp.cantidad) DESC")
    List<ProductoMasVendidoDTO> findTopSellingProductsRaw();

    long countByProductoId(Integer productoId);
}