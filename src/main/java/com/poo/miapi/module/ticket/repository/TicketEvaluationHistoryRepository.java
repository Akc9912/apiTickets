package com.poo.miapi.module.ticket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poo.miapi.module.ticket.model.TicketEvaluationHistory;

@Repository
public interface TicketEvaluationHistoryRepository extends JpaRepository<TicketEvaluationHistory, Integer> {

    /**
     * Encuentra todas las evaluaciones de un ticket específico
     */
    @Query("SELECT teh FROM TicketEvaluationHistory teh WHERE teh.ticket.id = :ticketId ORDER BY teh.evaluationDate DESC")
    List<TicketEvaluationHistory> findByTicketId(@Param("ticketId") int ticketId);

    /**
     * Encuentra todas las evaluaciones realizadas por un usuario
     */
    @Query("SELECT teh FROM TicketEvaluationHistory teh WHERE teh.evaluatorUser.id = :userId ORDER BY teh.evaluationDate DESC")
    List<TicketEvaluationHistory> findByEvaluatorUserId(@Param("userId") int userId);

    /**
     * Encuentra evaluaciones aprobadas o rechazadas
     */
    @Query("SELECT teh FROM TicketEvaluationHistory teh WHERE teh.wasApproved = :approved ORDER BY teh.evaluationDate DESC")
    List<TicketEvaluationHistory> findByApprovalStatus(@Param("approved") boolean approved);

    /**
     * Cuenta las evaluaciones de un ticket
     */
    @Query("SELECT COUNT(teh) FROM TicketEvaluationHistory teh WHERE teh.ticket.id = :ticketId")
    long countByTicketId(@Param("ticketId") int ticketId);

    /**
     * Obtiene la última evaluación de un ticket
     */
    @Query("SELECT teh FROM TicketEvaluationHistory teh WHERE teh.ticket.id = :ticketId ORDER BY teh.evaluationDate DESC LIMIT 1")
    TicketEvaluationHistory findLastEvaluationByTicketId(@Param("ticketId") int ticketId);
}
