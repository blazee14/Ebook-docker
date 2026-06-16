package com.api.capas.infrastructure.persistence.repositories;

import com.api.capas.infrastructure.persistence.entities.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Integer> {

    Optional<MetodoPago> findByNombreIgnoreCase(String nombre);
}