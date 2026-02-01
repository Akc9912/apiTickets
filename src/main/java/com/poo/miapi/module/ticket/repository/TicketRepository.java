package com.poo.miapi.module.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poo.miapi.module.ticket.enums.TicketStatus;
import com.poo.miapi.module.ticket.model.Ticket;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

  List<Ticket> findByEstado(TicketStatus estado);

  List<Ticket> findByCreatorId(int creator_id);

  List<Ticket> findByTittleContainingIgnoreCase(String word);

  // Buscar tickets por estado y creador
  List<Ticket> findByStatusAndCreatorId(TicketStatus status, int creator_id);

  // Métodos para estadísticas básicas
  long countByStatus(TicketStatus status);

}