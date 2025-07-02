package com.poo.miapi.repository;

import com.poo.miapi.model.EstadoTicket;
import com.poo.miapi.model.Ticket;
import com.poo.miapi.model.Tecnico;
import com.poo.miapi.model.Trabajador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    // Buscar tickets por estado
    List<Ticket> findByEstado(EstadoTicket estado);

    // Buscar tickets creados por un trabajador específico
    List<Ticket> findByCreador(Trabajador creador);

    // Buscar tickets asignados a un técnico específico
    List<Ticket> findByTecnicoActual(Tecnico tecnico);

    // Buscar tickets sin técnico asignado
    List<Ticket> findByTecnicoActualIsNull();

    // Buscar tickets con título parcial (opcional)
    List<Ticket> findByTituloContainingIgnoreCase(String titulo);
}
