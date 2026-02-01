package com.poo.miapi.module.ticket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poo.miapi.module.ticket.enums.RefundRequestStatus;
import com.poo.miapi.module.ticket.model.TicketRefundRequest;

@Repository
public interface TicketRefundRequestRepository extends JpaRepository<TicketRefundRequest, Integer> {

    // Buscar todas las solicitudes por estado
    List<TicketRefundRequest> findByStatus(RefundRequestStatus status);

    // Buscar solicitudes por desarrollador
    List<TicketRefundRequest> findByDeveloperId(int developerId);

    // Buscar solicitudes por desarrollador y estado
    List<TicketRefundRequest> findByDeveloperIdAndStatus(int developerId, RefundRequestStatus status);

    // Buscar solicitudes por ticket
    List<TicketRefundRequest> findByTicketId(int ticketId);

    // Verificar si existe una solicitud pendiente para un ticket específico
    boolean existsByTicketIdAndStatus(int ticketId, RefundRequestStatus status);
}
