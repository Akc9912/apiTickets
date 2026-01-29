package com.poo.miapi.repository.historial;

import com.poo.miapi.model.historial.HistorialValidacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialValidacionRepository extends JpaRepository<HistorialValidacion, Integer> {

    List<HistorialValidacion> findByUsuarioValidadorId(int usuarioValidadorId);

    List<HistorialValidacion> findByTicketId(int ticketId);

    int countByUsuarioValidadorId(int usuarioValidadorId);

    int countByTicketId(int ticketId);
}