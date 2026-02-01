package com.poo.miapi.module.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poo.miapi.module.user.model.DeveloperByTicket;

@Repository
public interface DeveloperByTicketRepository extends JpaRepository<DeveloperByTicket, Integer> {

    /**
     * Encuentra todo el historial de un desarrollador específico
     */
    @Query("SELECT dbt FROM DeveloperByTicket dbt WHERE dbt.developer.id = :developerId ORDER BY dbt.assignmentDate DESC")
    List<DeveloperByTicket> findByDeveloperId(@Param("developerId") int developerId);

    /**
     * Encuentra todo el historial de un ticket específico
     */
    @Query("SELECT dbt FROM DeveloperByTicket dbt WHERE dbt.ticket.id = :ticketId ORDER BY dbt.assignmentDate DESC")
    List<DeveloperByTicket> findByTicketId(@Param("ticketId") int ticketId);

    /**
     * Encuentra asignaciones activas (sin fecha de desasignación) de un
     * desarrollador
     */
    @Query("SELECT dbt FROM DeveloperByTicket dbt WHERE dbt.developer.id = :developerId AND dbt.unassignmentDate IS NULL")
    List<DeveloperByTicket> findActiveAssignmentsByDeveloperId(@Param("developerId") int developerId);

    /**
     * Encuentra asignaciones activas de un ticket específico
     */
    @Query("SELECT dbt FROM DeveloperByTicket dbt WHERE dbt.ticket.id = :ticketId AND dbt.unassignmentDate IS NULL")
    List<DeveloperByTicket> findActiveAssignmentsByTicketId(@Param("ticketId") int ticketId);

    /**
     * Cuenta el total de tickets atendidos por un desarrollador
     */
    @Query("SELECT COUNT(dbt) FROM DeveloperByTicket dbt WHERE dbt.developer.id = :developerId")
    long countByDeveloperId(@Param("developerId") int developerId);
}
