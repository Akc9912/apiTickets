package com.poo.miapi.module.user.service;

import com.poo.miapi.module.ticket.dto.TicketResponseDto;
import com.poo.miapi.module.ticket.enums.TicketStatus;
import com.poo.miapi.module.ticket.model.Ticket;
import com.poo.miapi.module.ticket.repository.TicketRepository;
import com.poo.miapi.module.user.dto.DeveloperResponseDto;
import com.poo.miapi.module.user.dto.SupportResponseDto;
import com.poo.miapi.module.user.dto.UserRequestDto;
import com.poo.miapi.module.user.dto.UserResponseDto;
import com.poo.miapi.module.user.model.Support;
import com.poo.miapi.module.user.repository.SupportRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.poo.miapi.shared.util.PasswordHelper;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SupportService {

    private final SupportRepository supportRepository;
    private final PasswordEncoder passwordEncoder;
    private final TicketRepository ticketRepository;

    @Value("${app.default-password}")
    private String defaultPassword;

    public SupportService(
            SupportRepository supportRepository,
            PasswordEncoder passwordEncoder,
            TicketRepository ticketRepository) {
        this.supportRepository = supportRepository;
        this.passwordEncoder = passwordEncoder;
        this.ticketRepository = ticketRepository;
    }

    // MÉTODOS PÚBLICOS
    // Buscar support por ID
    public Support findById(int id) {
        return supportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Support no encontrado"));
    }

    // Buscar support por email
    public Support findByEmail(String email) {
        return supportRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Support no encontrado"));
    }

    // Listar todos los support
    public List<SupportResponseDto> findAll() {
        return supportRepository.findAll().stream()
                .map(this::mapToSupportDto)
                .toList();
    }

    // Listar support activos
    public List<SupportResponseDto> findActive() {
        return supportRepository.findByActiveTrue().stream()
                .map(this::mapToSupportDto)
                .toList();
    }

    // Obtener datos del support
    public SupportResponseDto getDetails(int supportId) {
        Support support = findById(supportId);
        return mapToSupportDto(support);
    }

    // Editar datos del support
    public SupportResponseDto updateData(int supportId, UserRequestDto userDto) {
        Support support = findById(supportId);
        support.setName(userDto.getName());
        support.setLastName(userDto.getLastName());
        support.setEmail(userDto.getEmail());
        supportRepository.save(support);

        return mapToSupportDto(support);
    }

    // Resetear contraseña a la por defecto
    public SupportResponseDto resetPassword(int supportId) {
        Support support = findById(supportId);
        String rawPassword = PasswordHelper.generarPasswordPorDefecto(support.getLastName());
        support.setPassword(passwordEncoder.encode(rawPassword));
        support.setChangePassword(true);
        supportRepository.save(support);
        return mapToSupportDto(support);
    }

    /**
     * Evaluar ticket - El support acepta o rechaza la solución propuesta
     * 
     * @param ticketId  Ticket ID
     * @param supportId Support ID evaluando
     * @param approve   true para aceptar (CLOSED), false para rechazar
     *                  (REOPENED)
     * @param comment   Comentario de evaluación
     * @return Ticket actualizado
     */
    @Transactional
    public TicketResponseDto evaluateTicket(int ticketId, int supportId, boolean approve, String comment) {
        // Validar que el ticket existe
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with ID: " + ticketId));

        // Validar que el support existe y es el creador del ticket
        Support support = findById(supportId);
        if (ticket.getCreator() == null || ticket.getCreator().getId() != supportId) {
            throw new IllegalStateException("Only the ticket creator can evaluate it");
        }

        // Validar que el ticket esté en estado RESOLVED
        if (ticket.getStatus() != TicketStatus.RESOLVED) {
            throw new IllegalStateException("Only tickets in RESOLVED status can be evaluated");
        }

        // Actualizar el estado según la evaluación
        if (approve) {
            ticket.setStatus(TicketStatus.CLOSED);
        } else {
            ticket.setStatus(TicketStatus.REOPENED);
        }
        ticket.setUpdateDate(LocalDateTime.now());

        Ticket saved = ticketRepository.save(ticket);
        return mapTicketToDto(saved);
    }

    // Mapear ticket a DTO
    private TicketResponseDto mapTicketToDto(Ticket ticket) {
        return new TicketResponseDto(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getCreator() != null ? new UserResponseDto(
                        ticket.getCreator().getId(),
                        ticket.getCreator().getName(),
                        ticket.getCreator().getLastName(),
                        ticket.getCreator().getEmail(),
                        ticket.getCreator().getRole(),
                        ticket.getCreator().isChangePassword(),
                        ticket.getCreator().isActive(),
                        ticket.getCreator().isBlocked()) : null,
                ticket.getDeveloper() != null ? new DeveloperResponseDto(
                        ticket.getDeveloper().getId(),
                        ticket.getDeveloper().getName(),
                        ticket.getDeveloper().getLastName(),
                        ticket.getDeveloper().getEmail(),
                        ticket.getDeveloper().getRole(),
                        ticket.getDeveloper().isChangePassword(),
                        ticket.getDeveloper().isActive(),
                        ticket.getDeveloper().isBlocked(),
                        ticket.getDeveloper().getFailures(),
                        ticket.getDeveloper().getWarnings()) : null,
                ticket.getCreationDate(),
                ticket.getUpdateDate());
    }

    // MÉTODOS PRIVADOS/UTILIDADES
    private SupportResponseDto mapToSupportDto(Support support) {
        return new SupportResponseDto(
                support.getId(),
                support.getName(),
                support.getLastName(),
                support.getEmail(),
                support.getRole(),
                support.isChangePassword(),
                support.isActive(),
                support.isBlocked());
    }
}
