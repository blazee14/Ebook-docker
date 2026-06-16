package com.api.capas.infrastructure.persistence.repositories;

import com.api.capas.infrastructure.persistence.entities.Pedido;
import com.api.capas.infrastructure.persistence.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByUsuario(Usuario usuario);

    List<Pedido> findByEstado(Pedido.EstadoPedido estado);
}