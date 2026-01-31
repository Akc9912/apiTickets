package com.poo.miapi.module.audit.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poo.miapi.module.audit.model.HistorialValidacion;

import java.util.List;

@Repository
public interface HistorialValidacionRepository extends JpaRepository<HistorialValidacion, Integer> {

    List<HistorialValidacion> findByUsuarioValidadorId(int usuarioValidadorId);

    List<HistorialValidacion> findByTicketId(int ticketId);

    int countByUsuarioValidadorId(int usuarioValidadorId);

    int countByTicketId(int ticketId);
}