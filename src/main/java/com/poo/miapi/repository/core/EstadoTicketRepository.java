package com.poo.miapi.repository.core;

import com.poo.miapi.model.core.EstadoTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstadoTicketRepository extends JpaRepository<EstadoTicket, String> {

    List<EstadoTicket> findByNombreContainingIgnoreCase(String nombre);

    boolean existsById(String id);

    // Ya tienes findById por defecto
}