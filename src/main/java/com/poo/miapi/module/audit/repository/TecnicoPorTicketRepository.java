package com.poo.miapi.module.audit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poo.miapi.module.audit.model.TecnicoPorTicket;
import com.poo.miapi.module.ticket.model.Ticket;
import com.poo.miapi.module.user.model.Tecnico;

@Repository
public interface TecnicoPorTicketRepository extends JpaRepository<TecnicoPorTicket, Integer> {

    List<TecnicoPorTicket> findByTecnico(Tecnico tecnico);

    List<TecnicoPorTicket> findByTicket(Ticket ticket);

    Optional<TecnicoPorTicket> findByTecnicoAndTicket(Tecnico tecnico, Ticket ticket);

    Optional<TecnicoPorTicket> findByTecnicoAndTicketAndFechaDesasignacionIsNull(Tecnico tecnico, Ticket ticket);

    List<TecnicoPorTicket> findByTecnicoId(int idTecnico);

    List<TecnicoPorTicket> findByTicketId(int idTicket);

    Optional<TecnicoPorTicket> findByTecnicoIdAndTicketIdAndFechaDesasignacionIsNull(int tecnicoId, int ticketId);
}
