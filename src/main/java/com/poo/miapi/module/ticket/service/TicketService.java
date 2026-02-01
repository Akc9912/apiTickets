package com.poo.miapi.module.ticket.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import com.poo.miapi.module.ticket.dto.TicketRequestDto;
import com.poo.miapi.module.ticket.dto.TicketResponseDto;
import com.poo.miapi.module.ticket.enums.TicketStatus;
import com.poo.miapi.module.ticket.model.Ticket;
import com.poo.miapi.module.ticket.repository.TicketRepository;
import com.poo.miapi.module.user.dto.UserResponseDto;
import com.poo.miapi.module.user.model.Support;
import com.poo.miapi.module.user.model.User;
import com.poo.miapi.module.user.enums.UserRole;
import com.poo.miapi.module.user.service.UserService;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserService userService;

    public TicketService(
            TicketRepository ticketRepository,
            UserService userService) {
        this.ticketRepository = ticketRepository;
        this.userService = userService;
    }

    /**
     * Create ticket with specific creator
     * 
     * @param dto     Ticket data
     * @param creator User creating the ticket
     * @return Created ticket DTO
     */
    public TicketResponseDto createTicket(TicketRequestDto dto, User creator) {
        Ticket ticket = new Ticket(dto.getTittle(), dto.getDescription(), creator);
        Ticket saved = ticketRepository.save(ticket);
        return mapToDto(saved);
    }

    /**
     * Find ticket by ID
     * 
     * @param id Ticket ID
     * @return Ticket DTO
     * @throws EntityNotFoundException if ticket not found
     */
    public TicketResponseDto findById(int id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
        return mapToDto(ticket);
    }

    /**
     * List all tickets
     * 
     * @return List of ticket DTOs
     */
    public List<TicketResponseDto> findAll() {
        return ticketRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * List tickets by status
     * 
     * @param status Ticket status
     * @return List of ticket DTOs
     */
    public List<TicketResponseDto> findByStatus(TicketStatus status) {
        return ticketRepository.findByEstado(status).stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * List tickets by creator
     * 
     * @param creatorId Creator user ID
     * @return List of ticket DTOs
     */
    public List<TicketResponseDto> findByCreator(int creatorId) {
        return ticketRepository.findByCreatorId(creatorId).stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * List tickets by creator and status
     * 
     * @param creatorId Creator user ID
     * @param status    Ticket status
     * @return List of ticket DTOs
     */
    public List<TicketResponseDto> findByCreatorAndStatus(int creatorId, TicketStatus status) {
        return ticketRepository.findByStatusAndCreatorId(status, creatorId).stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * Search tickets by title
     * 
     * @param keyword Search keyword
     * @return List of ticket DTOs
     */
    public List<TicketResponseDto> searchByTitle(String keyword) {
        return ticketRepository.findByTittleContainingIgnoreCase(keyword).stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * Update ticket status
     * 
     * @param ticketId  Ticket ID
     * @param newStatus New status
     * @return Updated ticket DTO
     * @throws EntityNotFoundException if ticket not found
     */
    public TicketResponseDto updateStatus(int ticketId, TicketStatus newStatus) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        ticket.setEstado(newStatus);
        ticket.setUpdateDate(LocalDateTime.now());
        Ticket updated = ticketRepository.save(ticket);
        return mapToDto(updated);
    }

    /**
     * Save ticket
     * 
     * @param ticket Ticket entity
     * @return Saved ticket DTO
     */
    public TicketResponseDto save(Ticket ticket) {
        Ticket saved = ticketRepository.save(ticket);
        return mapToDto(saved);
    }

    /**
     * Create ticket with role-based logic
     * 
     * @param dto  Ticket data
     * @param user Authenticated user
     * @return Created ticket DTO
     */
    public TicketResponseDto createTicketWithRole(TicketRequestDto dto, User user) {
        User ticketCreator = null;

        if (user.getRole() == UserRole.SUPPORT) {
            ticketCreator = user;
        } else if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.SUPERADMIN) {
            if (dto.getCreatorId() != 0) {
                ticketCreator = userService.findById(dto.getCreatorId());
                if (!(ticketCreator instanceof Support)) {
                    throw new IllegalArgumentException("Creator must be a Support user");
                }
            } else {
                ticketCreator = user;
            }
        } else {
            throw new IllegalStateException("Role not authorized to create tickets");
        }

        return createTicket(dto, ticketCreator);
    }

    /**
     * Verify if ticket belongs to user
     * 
     * @param ticketId Ticket ID
     * @param userId   User ID
     * @return true if ticket belongs to user
     */
    public boolean isTicketOwnedByUser(int ticketId, int userId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
        return ticket.getCreator() != null && ticket.getCreator().getId() == userId;
    }

    /**
     * Reopen ticket
     * 
     * @param ticketId Ticket ID
     * @param comment  Reopen comment
     * @param userId   User ID requesting reopen
     * @return Reopened ticket DTO
     */
    public TicketResponseDto reopenTicket(int ticketId, String comment, int userId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
        User user = userService.findById(userId);

        if (ticket.getEstado() != TicketStatus.FINALIZED) {
            throw new IllegalArgumentException("Ticket is not finalized, cannot reopen");
        }

        UserRole role = user.getRole();
        boolean isSupport = role == UserRole.SUPPORT;
        boolean isAdmin = role == UserRole.ADMIN;
        boolean isSuperAdmin = role == UserRole.SUPERADMIN;

        if (isSupport) {
            if (ticket.getCreator() == null || ticket.getCreator().getId() != user.getId()) {
                throw new SecurityException("You cannot reopen tickets you did not create");
            }
        } else if (!(isAdmin || isSuperAdmin)) {
            throw new SecurityException("You do not have permission to reopen tickets");
        }

        ticket.setEstado(TicketStatus.REOPENED);
        ticket.setUpdateDate(LocalDateTime.now());
        ticketRepository.save(ticket);

        return mapToDto(ticket);
    }

    /**
     * Map Ticket entity to DTO
     * 
     * @param ticket Ticket entity
     * @return Ticket DTO
     */
    public TicketResponseDto mapToDto(Ticket ticket) {
        UserResponseDto creatorDto = null;
        if (ticket.getCreator() != null) {
            User creator = ticket.getCreator();
            creatorDto = new UserResponseDto(
                    creator.getId(),
                    creator.getName(),
                    creator.getLastName(),
                    creator.getEmail(),
                    creator.getRole(),
                    creator.isChangePassword(),
                    creator.isActive(),
                    creator.isBlocked());
        }

        return new TicketResponseDto(
                ticket.getId(),
                ticket.getTtittle(),
                ticket.getDescripcion(),
                ticket.getEstado(),
                creatorDto,
                null, // Developer will be set by DeveloperService
                ticket.getCreationDate(),
                ticket.getUpdateDate());
    }
}
