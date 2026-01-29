package com.poo.miapi.repository.core;

import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.enums.EstadoTicket;
import com.poo.miapi.model.core.Tecnico;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

  // Métodos para estadísticas básicas
  long countByEstado(EstadoTicket estado);

  // ========================================
  // MÉTODOS PARA ESTADÍSTICAS AVANZADAS
  // ========================================

  // Contar tickets creados en un rango de fechas
  long countByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);

  // Contar tickets por estado y rango de fechas de actualización
  long countByEstadoAndFechaUltimaActualizacionBetween(EstadoTicket estado, LocalDateTime inicio, LocalDateTime fin);

  // Contar tickets por múltiples estados
  long countByEstadoIn(List<EstadoTicket> estados);

  // Obtener tiempos de resolución en un período
  @Query("""
      SELECT t.id,
             TIMESTAMPDIFF(HOUR, t.fechaCreacion, t.fechaUltimaActualizacion) as horasResolucion
      FROM Ticket t
      WHERE t.estado = 'FINALIZADO'
      AND t.fechaUltimaActualizacion BETWEEN :inicio AND :fin
      """)
  List<Object[]> findTiemposResolucionEnPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

  // Tickets creados en un período específico
  List<Ticket> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);

  // Tickets actualizados en un período específico
  List<Ticket> findByFechaUltimaActualizacionBetween(LocalDateTime inicio, LocalDateTime fin);

  // Tickets por estado en un rango de fechas
  List<Ticket> findByEstadoAndFechaCreacionBetween(EstadoTicket estado, LocalDateTime inicio, LocalDateTime fin);
}
