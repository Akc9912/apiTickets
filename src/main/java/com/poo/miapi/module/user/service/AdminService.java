package com.poo.miapi.module.user.service;

import com.poo.miapi.module.ticket.dto.TicketResponseDto;
import com.poo.miapi.module.ticket.model.EstadoTicket;
import com.poo.miapi.module.ticket.model.Ticket;
import com.poo.miapi.module.user.dto.UsuarioRequestDto;
import com.poo.miapi.module.user.model.*;
import com.poo.miapi.shared.events.enums.AccionAuditoria;
import com.poo.miapi.shared.events.enums.CategoriaAuditoria;
import com.poo.miapi.shared.events.enums.SeveridadAuditoria;
import com.poo.miapi.module.ticket.repository.TicketRepository;
import com.poo.miapi.module.audit.model.TecnicoPorTicket;
import com.poo.miapi.module.audit.repository.TecnicoPorTicketRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import com.poo.miapi.module.audit.service.AuditoriaService;

@Service
public class AdminService {

    private final TicketRepository ticketRepository;
    private final TecnicoPorTicketRepository tecnicoPorTicketRepository;
    private final TecnicoService tecnicoService;
    private final AuditoriaService auditoriaService;

    public AdminService(
            TicketRepository ticketRepository,
            TecnicoPorTicketRepository tecnicoPorTicketRepository,
            TecnicoService tecnicoService,
            AuditoriaService auditoriaService) {
        this.ticketRepository = ticketRepository;
        this.tecnicoPorTicketRepository = tecnicoPorTicketRepository;
        this.tecnicoService = tecnicoService;
        this.auditoriaService = auditoriaService;
    }

    // MÉTODOS PÚBLICOS
    // Reabrir ticket
    public TicketResponseDto reabrirTicket(int idTicket, String comentario) {
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado con ID: " + idTicket));

        if (ticket.getEstado() != EstadoTicket.FINALIZADO) {
            throw new IllegalArgumentException("El ticket no está cerrado, no se puede reabrir");
        }

        Tecnico tecnicoActual = ticket.getTecnicoActual();
        if (tecnicoActual == null) {
            throw new IllegalArgumentException("No hay técnico asignado al ticket, no se puede reabrir");
        }

        TecnicoPorTicket entradaHistorial = tecnicoPorTicketRepository
                .findByTecnicoAndTicket(tecnicoActual, ticket)
                .orElseThrow(
                        () -> new IllegalArgumentException("No se encontró historial para este ticket en el técnico"));

        entradaHistorial.setEstadoFinal(EstadoTicket.REABIERTO);
        entradaHistorial.setFechaDesasignacion(LocalDateTime.now());
        entradaHistorial.setComentario(comentario);
        tecnicoPorTicketRepository.save(entradaHistorial);

        tecnicoService.marcarMarca(tecnicoActual.getId(), comentario, ticket);

        ticket.setEstado(EstadoTicket.REABIERTO);
        ticket.setFechaUltimaActualizacion(LocalDateTime.now());
        ticketRepository.save(ticket);

        // Auditar reapertura de ticket por admin
        auditoriaService.registrarAccion(
                null, // No tenemos usuario admin aquí, se puede mejorar pasándolo como parámetro
                AccionAuditoria.REOPEN_TICKET,
                "TICKET",
                ticket.getId(),
                "Ticket reabierto por admin: " + comentario,
                EstadoTicket.FINALIZADO,
                EstadoTicket.REABIERTO,
                CategoriaAuditoria.BUSINESS,
                SeveridadAuditoria.HIGH);

        return mapToTicketDto(ticket);
    }

    // Validar datos del usuario
    public void validarDatosUsuario(UsuarioRequestDto usuarioDto) {
        if (usuarioDto.getNombre() == null || usuarioDto.getApellido() == null ||
                usuarioDto.getEmail() == null || usuarioDto.getRol() == null) {
            throw new IllegalArgumentException("Todos los campos son obligatorios");
        }
        validarRol(usuarioDto.getRol());
    }

    // Crear usuario por rol
    public Usuario crearUsuarioPorRol(UsuarioRequestDto dto) {
        switch (dto.getRol()) {
            case ADMIN:
                return new Admin(dto.getNombre(), dto.getApellido(), dto.getEmail());
            case TECNICO:
                return new Tecnico(dto.getNombre(), dto.getApellido(), dto.getEmail());
            case TRABAJADOR:
                return new Trabajador(dto.getNombre(), dto.getApellido(), dto.getEmail());
            default:
                throw new IllegalArgumentException("Rol no válido: " + dto.getRol());
        }
    }

    // Crear Admin
    public Admin crearAdmin(String nombre, String apellido, String email) {
        return new Admin(nombre, apellido, email);
    }

    // Crear Técnico
    public Tecnico crearTecnico(String nombre, String apellido, String email) {
        return new Tecnico(nombre, apellido, email);
    }

    // Crear Trabajador
    public Trabajador crearTrabajador(String nombre, String apellido, String email) {
        return new Trabajador(nombre, apellido, email);
    }

    // MÉTODOS PRIVADOS/UTILIDADES
    // Validar rol
    private void validarRol(Rol rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        if (rol == Rol.SUPER_ADMIN) {
            throw new IllegalArgumentException("No se puede asignar rol SUPER_ADMIN");
        }
    }

    // Método auxiliar para mapear Ticket a DTO
    private TicketResponseDto mapToTicketDto(Ticket ticket) {
        return new TicketResponseDto(
                ticket.getId(),
                ticket.getTitulo(),
                ticket.getDescripcion(),
                ticket.getEstado(),
                ticket.getCreador() != null ? ticket.getCreador().getNombre() : null,
                ticket.getTecnicoActual() != null ? ticket.getTecnicoActual().getNombre() : null,
                ticket.getFechaCreacion(),
                ticket.getFechaUltimaActualizacion());
    }

}
