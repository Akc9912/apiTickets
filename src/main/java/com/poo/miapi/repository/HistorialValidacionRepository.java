package com.poo.miapi.repository;

import com.poo.miapi.model.historial.HistorialValidacionTrabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialValidacionRepository extends JpaRepository<HistorialValidacionTrabajador, Long> {
    List<HistorialValidacionTrabajador> findByTrabajadorId(int trabajadorId);

    List<HistorialValidacionTrabajador> findByTicketId(int ticketId);
}