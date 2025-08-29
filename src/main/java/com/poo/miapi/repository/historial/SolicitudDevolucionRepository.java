package com.poo.miapi.repository.historial;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poo.miapi.model.historial.SolicitudDevolucion;

public interface SolicitudDevolucionRepository extends JpaRepository<SolicitudDevolucion, Integer> {
    List<SolicitudDevolucion> findByTecnicoId(int tecnicoId);

    List<SolicitudDevolucion> findByTicketId(int ticketId);

    int countByTecnicoId(int tecnicoId);
}
