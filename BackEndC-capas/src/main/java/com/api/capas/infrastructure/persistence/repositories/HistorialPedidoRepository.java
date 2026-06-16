package com.api.capas.infrastructure.persistence.repositories;

import com.api.capas.infrastructure.persistence.entities.HistorialPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialPedidoRepository extends JpaRepository<HistorialPedido, Integer> {
}