package com.poo.miapi.service.core;

import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.dto.usuarios.AdminResponseDto;
import com.poo.miapi.dto.usuarios.TecnicoResponseDto;
import com.poo.miapi.dto.usuarios.TrabajadorResponseDto;
import com.poo.miapi.dto.usuarios.UsuarioRequestDto;
import com.poo.miapi.dto.usuarios.UsuarioResponseDto;
import com.poo.miapi.model.core.*;
import com.poo.miapi.model.enums.EstadoTicket;
import com.poo.miapi.model.enums.Rol;
import com.poo.miapi.model.historial.TecnicoPorTicket;
import com.poo.miapi.repository.core.TicketRepository;
import com.poo.miapi.repository.historial.TecnicoPorTicketRepository;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AdminService {

    private final TicketRepository ticketRepository;
    private final TecnicoPorTicketRepository tecnicoPorTicketRepository;
    private final TecnicoService tecnicoService;


    public AdminService(
            TicketRepository ticketRepository,
            TecnicoPorTicketRepository tecnicoPorTicketRepository,
            TecnicoService tecnicoService) {
        this.ticketRepository = ticketRepository;
        this.tecnicoPorTicketRepository = tecnicoPorTicketRepository;
        this.tecnicoService = tecnicoService;
    }



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

        return mapToTicketDto(ticket);
    }


    private void validarDatosUsuario(UsuarioRequestDto usuarioDto) {
        if (usuarioDto.getNombre() == null || usuarioDto.getApellido() == null ||
                usuarioDto.getEmail() == null || usuarioDto.getRol() == null) {
            throw new IllegalArgumentException("Todos los campos son obligatorios");
        }
        validarRol(usuarioDto.getRol());
    }

    private void validarRol(Rol rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        if (rol == Rol.SUPER_ADMIN) {
            throw new IllegalArgumentException("No se puede asignar rol SUPER_ADMIN");
        }
    }

    private Usuario crearUsuarioPorRol(UsuarioRequestDto dto) {
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

    public Admin crearAdmin(String nombre, String apellido, String email) {
        return new Admin(nombre, apellido, email);
    }

    public Tecnico crearTecnico(String nombre, String apellido, String email) {
        return new Tecnico(nombre, apellido, email);
    }

    public Trabajador crearTrabajador(String nombre, String apellido, String email) {
        return new Trabajador(nombre, apellido, email);
    }

    // Mapeo de entidad Usuario a DTO
    /* 
    private UsuarioResponseDto mapToUsuarioDto(Usuario usuario) {
       if (usuario instanceof Admin) {
            return new AdminResponseDto(usuario.getId(), usuario.getNombre(), usuario.getApellido(),
                    usuario.getEmail(), usuario.getRol(), usuario.isCambiarPass(), usuario.isActivo(),
                    usuario.isBloqueado());
        } else if (usuario instanceof Tecnico) {
            return new TecnicoResponseDto(usuario.getId(), usuario.getNombre(), usuario.getApellido(),
                    usuario.getEmail(), usuario.getRol(), usuario.isCambiarPass(), usuario.isActivo(),
                    usuario.isBloqueado(), ((Tecnico)usuario).getFallas(), ((Tecnico)usuario).getMarcas());
        } else if (usuario instanceof Trabajador) {
            return new TrabajadorResponseDto(usuario.getId(), usuario.getNombre(), usuario.getApellido(),
                    usuario.getEmail(), usuario.getRol(), usuario.isCambiarPass(), usuario.isActivo(),
                    usuario.isBloqueado());
        } else {
            return new UsuarioResponseDto(usuario.getId(), usuario.getNombre(), usuario.getApellido(),
                    usuario.getEmail(), usuario.getRol(), usuario.isCambiarPass(), usuario.isActivo(),
                    usuario.isBloqueado());
        }
    }
    */

    // Mapeo de entidad Ticket a DTO
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
