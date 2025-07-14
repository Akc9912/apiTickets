package com.poo.miapi.repository.historial;

import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.historial.TecnicoPorTicket;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TecnicoPorTicketRepository extends JpaRepository<TecnicoPorTicket, Integer> {

    List<TecnicoPorTicket> findByTecnico(Tecnico tecnico);

    List<TecnicoPorTicket> findByTicket(Ticket ticket);

    Optional<TecnicoPorTicket> findByTecnicoAndTicket(Tecnico tecnico, Ticket ticket);

    List<TecnicoPorTicket> findByTecnicoId(int idTecnico);

    List<TecnicoPorTicket> findByTicketId(int idTicket);
}