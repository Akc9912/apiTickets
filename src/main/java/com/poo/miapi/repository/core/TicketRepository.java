package com.poo.miapi.repository.core;

import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.EstadoTicket;
import com.poo.miapi.model.core.Tecnico;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

  List<Ticket> findByEstado(EstadoTicket estado);

  List<Ticket> findByCreadorId(int idTrabajador);

  List<Ticket> findByTituloContainingIgnoreCase(String palabra);

  // Buscar tickets asignados actualmente a un técnico
  @Query("""
          SELECT DISTINCT t
          FROM Ticket t
          JOIN t.historialTecnicos h
          WHERE h.tecnico = :tecnico
            AND h.fechaDesasignacion IS NULL
      """)
  List<Ticket> findByTecnicoActual(@Param("tecnico") Tecnico tecnico);

  // Buscar tickets por estado y técnico actual (usando historial)
  @Query("""
          SELECT DISTINCT t
          FROM Ticket t
          JOIN t.historialTecnicos h
          WHERE t.estado = :estado
            AND h.tecnico = :tecnico
            AND h.fechaDesasignacion IS NULL
      """)
  List<Ticket> findByEstadoAndTecnicoActual(@Param("estado") EstadoTicket estado, @Param("tecnico") Tecnico tecnico);

  // Buscar tickets por estado y creador
  List<Ticket> findByEstadoAndCreadorId(EstadoTicket estado, int idTrabajador);

  // Métodos para estadísticas de SuperAdminService
  long countByEstado(EstadoTicket estado);
}