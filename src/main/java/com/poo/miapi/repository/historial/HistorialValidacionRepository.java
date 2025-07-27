package com.poo.miapi.repository.historial;

import com.poo.miapi.model.historial.HistorialValidacionTrabajador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialValidacionRepository extends JpaRepository<HistorialValidacionTrabajador, Integer> {

    List<HistorialValidacionTrabajador> findByTrabajadorId(int trabajadorId);

    List<HistorialValidacionTrabajador> findByTicketId(int ticketId);

    int countByTrabajadorId(int trabajadorId);

    int countByTicketId(int ticketId);
}