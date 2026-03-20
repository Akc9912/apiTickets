package com.poo.miapi.module.ticket.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poo.miapi.module.ticket.dto.TicketRefundRequestResponseDto;
import com.poo.miapi.module.ticket.enums.RefundRequestStatus;
import com.poo.miapi.module.ticket.enums.TicketStatus;
import com.poo.miapi.module.ticket.model.Ticket;
import com.poo.miapi.module.ticket.model.TicketRefundRequest;
import com.poo.miapi.module.ticket.repository.TicketRefundRequestRepository;
import com.poo.miapi.module.ticket.repository.TicketRepository;
import com.poo.miapi.module.user.dto.UserResponseDto;
import com.poo.miapi.module.user.model.Admin;
import com.poo.miapi.module.user.model.Developer;
import com.poo.miapi.module.user.model.User;
import com.poo.miapi.module.user.service.UserService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TicketRefundRequestService {

    private final TicketRefundRequestRepository refundRequestRepository;
    private final TicketRepository ticketRepository;
    private final UserService userService;
    private final TicketService ticketService;

    public TicketRefundRequestService(
            TicketRefundRequestRepository refundRequestRepository,
            TicketRepository ticketRepository,
            UserService userService,
            TicketService ticketService) {
        this.refundRequestRepository = refundRequestRepository;
        this.ticketRepository = ticketRepository;
        this.userService = userService;
        this.ticketService = ticketService;
    }

    /**
     * Crear una solicitud de devolución de ticket por parte de un desarrollador
     */
    @Transactional
    public TicketRefundRequest createRefundRequest(int developerId, int ticketId, String reason) {
        // Validar desarrollador
        Developer developer = (Developer) userService.findById(developerId);
        if (developer == null) {
            throw new EntityNotFoundException("Developer not found with ID: " + developerId);
        }

        // Validar ticket
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) {
            throw new EntityNotFoundException("Ticket not found");
        }

        // Validar que el ticket esté asignado al desarrollador
        if (ticket.getDeveloper() == null || ticket.getDeveloper().getId() != developerId) {
            throw new IllegalStateException("Ticket is not assigned to this developer");
        }

        // Validar que el ticket esté en estado IN_PROGRESS
        if (ticket.getStatus() != TicketStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only tickets in IN_PROGRESS status can be returned");
        }

        // Validar que no exista una solicitud pendiente para este ticket
        if (refundRequestRepository.existsByTicketIdAndStatus(ticketId, RefundRequestStatus.PENDING)) {
            throw new IllegalStateException("A pending refund request already exists for this ticket");
        }

        // Crear la solicitud
        TicketRefundRequest refundRequest = new TicketRefundRequest(developer, ticket, reason);
        return refundRequestRepository.save(refundRequest);
    }

    /**
     * ResponseDto> findAll() {
     * return refundRequestRepository.findAll().stream()
     * .map(this::mapToDto)
     * .collect(Collectors.toList());
     * }
     * 
     * /**
     * Obtener solicitudes por estado
     */
    public List<TicketRefundRequestResponseDto> findByStatus(RefundRequestStatus status) {
        return refundRequestRepository.findByStatus(status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtener solicitudes pendientes
     */
    public List<TicketRefundRequestResponseDto> findPendingRequests() {
        return findByStatus(RefundRequestStatus.PENDING);
    }

    /**
     * Obtener solicitudes por desarrollador
     */
    public List<TicketRefundRequestResponseDto> findByDeveloper(int developerId) {
        return refundRequestRepository.findByDeveloperId(developerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtener una solicitud por ID
     */
    public TicketRefundRequestResponseDto findByIdDto(int id) {
        TicketRefundRequest refundRequest = refundRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Refund request not found with ID: " + id));
        return mapToDto(refundRequest);
    }

    /**
     * ResponseDto processRefundRequest(int requestId, int adminId, boolean approve,
     * String resolutionComment) {
     * // Validar solicitud
     * TicketRefundRequest refundRequest = findById(requestId);
     * 
     * // Validar que la solicitud esté pendiente
     * if (refundRequest.getStatus() != RefundRequestStatus.PENDING) {
     * throw new IllegalStateException("Only pending requests can be processed");
     * }
     * 
     * // Validar admin
     * Admin admin = (Admin) userService.findById(adminId);
     * if (admin == null) {
     * throw new EntityNotFoundException("Admin not found with ID: " + adminId);
     * }
     * 
     * // Actualizar la solicitud
     * refundRequest.setStatus(approve ? RefundRequestStatus.APPROVED :
     * RefundRequestStatus.REJECTED);
     * refundRequest.setResolvedBy(admin);
     * refundRequest.setResolutionDate(LocalDateTime.now());
     * refundRequest.setResolutionComment(resolutionComment);
     * 
     * // Si se aprueba, liberar el ticket (cambiar a PENDING y eliminar
     * asignación)
     * if (approve) {
     * Ticket ticket = refundRequest.getTicket();
     * ticket.setStatus(TicketStatus.PENDING);
     * ticket.setDeveloper(null);
     * ticketRepository.save(ticket);
     * }
     * 
     * TicketRefundRequest saved = refundRequestRepository.save(refundRequest);
     * return mapToDto(saved);
     * }
     * 
     * /**
     * Convertir entidad a DTO
     */
    private TicketRefundRequestResponseDto mapToDto(TicketRefundRequest refundRequest) {
        TicketRefundRequestResponseDto dto = new TicketRefundRequestResponseDto();
        dto.setId(refundRequest.getId());
        dto.setReason(refundRequest.getReason());
        dto.setStatus(refundRequest.getStatus());
        dto.setRequestDate(refundRequest.getRequestDate());
        dto.setResolutionDate(refundRequest.getResolutionDate());
        dto.setResolutionComment(refundRequest.getResolutionComment());

        // Mapear desarrollador
        if (refundRequest.getDeveloper() != null) {
            dto.setDeveloper(mapUserToDto(refundRequest.getDeveloper()));
        }

        // Mapear ticket usando el servicio de tickets
        if (refundRequest.getTicket() != null) {
            dto.setTicket(ticketService.mapToDto(refundRequest.getTicket()));
        }

        // Mapear admin que resolvió
        if (refundRequest.getResolvedBy() != null) {
            dto.setResolvedBy(mapUserToDto(refundRequest.getResolvedBy()));
        }

        return dto;
    }

    /**
     * Mapear usuario a DTO básico
     */
    private UserResponseDto mapUserToDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        dto.setBlocked(user.isBlocked());
        return dto;
    }

    /**
     * Procesar solicitud de devolución
     */
    @Transactional
    public TicketRefundRequestResponseDto processRefundRequest(int requestId, int adminId, boolean approve,
            String resolutionComment) {
        // Validar solicitud
        TicketRefundRequest refundRequest = refundRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Refund request not found with ID: " + requestId));

        // Validar que la solicitud esté pendiente
        if (refundRequest.getStatus() != RefundRequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be processed");
        }

        // Validar admin
        Admin admin = (Admin) userService.findById(adminId);
        if (admin == null) {
            throw new EntityNotFoundException("Admin not found with ID: " + adminId);
        }

        // Actualizar la solicitud
        refundRequest.setStatus(approve ? RefundRequestStatus.APPROVED : RefundRequestStatus.REJECTED);
        refundRequest.setResolvedBy(admin);
        refundRequest.setResolutionDate(LocalDateTime.now());
        refundRequest.setResolutionComment(resolutionComment);

        // Si se aprueba, liberar el ticket (cambiar a PENDING y eliminar
        // asignación)
        if (approve) {
            Ticket ticket = refundRequest.getTicket();
            ticket.setStatus(TicketStatus.PENDING);
            ticket.setDeveloper(null);
            ticketRepository.save(ticket);
        }

        TicketRefundRequest saved = refundRequestRepository.save(refundRequest);
        return mapToDto(saved);
    }

    /**
     * Guardar solicitud de devolución
     */
    @SuppressWarnings("null")
    @Transactional
    public TicketRefundRequest save(TicketRefundRequest refundRequest) {
        return refundRequestRepository.save(refundRequest);
    }
}
