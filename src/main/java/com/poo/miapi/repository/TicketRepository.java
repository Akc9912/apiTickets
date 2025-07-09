package com.poo.miapi.repository;

import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.EstadoTicket;
import com.poo.miapi.model.core.Tecnico;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findByEstado(EstadoTicket estado);

    List<Ticket> findByCreadorId(int idTrabajador);

    List<Ticket> findByTituloContainingIgnoreCase(String palabra);

    List<Ticket> findByTecnicoActual(Tecnico tecnico);
}