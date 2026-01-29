package com.poo.miapi.service.core;

import com.poo.miapi.dto.tecnico.TecnicoResponseDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.dto.tecnico.IncidenteTecnicoResponseDto;
import com.poo.miapi.model.core.*;
import com.poo.miapi.model.enums.EstadoSolicitud;
import com.poo.miapi.model.enums.EstadoTicket;
import com.poo.miapi.model.historial.*;
import com.poo.miapi.repository.core.TecnicoRepository;
import com.poo.miapi.repository.core.TicketRepository;
import com.poo.miapi.repository.historial.IncidenteTecnicoRepository;
import com.poo.miapi.repository.historial.SolicitudDevolucionRepository;
import com.poo.miapi.service.historial.TecnicoPorTicketService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.poo.miapi.service.auditoria.AuditoriaService;
import com.poo.miapi.model.enums.AccionAuditoria;
import com.poo.miapi.model.enums.CategoriaAuditoria;
import com.poo.miapi.model.enums.SeveridadAuditoria;
import com.poo.miapi.model.enums.TipoIncidente;

import java.util.List;

@Service
public class TecnicoService {

    private final TecnicoRepository tecnicoRepository;
    private final TicketRepository ticketRepository;
    private final TecnicoPorTicketService tecnicoPorTicketService;
    private final IncidenteTecnicoRepository incidenteTecnicoRepository;
    private final SolicitudDevolucionRepository solicitudDevolucionRepository;
    private final AuditoriaService auditoriaService;

    public TecnicoService(
            TecnicoRepository tecnicoRepository,
            TicketRepository ticketRepository,
            IncidenteTecnicoRepository incidenteTecnicoRepository,
            TecnicoPorTicketService tecnicoPorTicketService,
            SolicitudDevolucionRepository solicitudDevolucionRepository,
            AuditoriaService auditoriaService) {
        this.tecnicoRepository = tecnicoRepository;
        this.ticketRepository = ticketRepository;
        this.incidenteTecnicoRepository = incidenteTecnicoRepository;
        this.tecnicoPorTicketService = tecnicoPorTicketService;
        this.solicitudDevolucionRepository = solicitudDevolucionRepository;
        this.auditoriaService = auditoriaService;
    }

    // MÉTODOS PÚBLICOS
    // Buscar técnico por ID
    public Tecnico buscarPorId(int idTecnico) {
        return tecnicoRepository.findById(idTecnico)
                .orElseThrow(() -> new EntityNotFoundException("Técnico no encontrado"));
    }

    // Sumar falla al técnico
    public void sumarFalla(Tecnico tecnico) {
        tecnico.setFallas(tecnico.getFallas() + 1);
        if (tecnico.getFallas() >= 2) {
            tecnico.setBloqueado(true);
        }
        tecnicoRepository.save(tecnico);
    }

    // Sumar marca al técnico
    public void sumarMarca(Tecnico tecnico) {
        tecnico.setMarcas(tecnico.getMarcas() + 1);
        if (tecnico.getMarcas() >= 2) {
            tecnico.setMarcas(0);
            sumarFalla(tecnico);
        }
        tecnicoRepository.save(tecnico);
    }

    // Reiniciar fallas y marcas del técnico
    public void reiniciarFallasYMarcas(Tecnico tecnico) {
        tecnico.setFallas(0);
        tecnico.setMarcas(0);
        tecnico.setBloqueado(false);
        tecnicoRepository.save(tecnico);
    }

    // Reiniciar fallas y marcas por ID de técnico
    public void reiniciarFallasYMarcas(int idTecnico) {
        Tecnico tecnico = buscarPorId(idTecnico);
        reiniciarFallasYMarcas(tecnico);
    }

    // Marcar falla al técnico
    public void marcarFalla(int idTecnico, String motivo, Ticket ticket) {
        Tecnico tecnico = buscarPorId(idTecnico);
        sumarFalla(tecnico);

        if (tecnico.getFallas() >= 3) {
            tecnico.setBloqueado(true);
        }

        IncidenteTecnico incidente = new IncidenteTecnico(tecnico, ticket, TipoIncidente.FALLA,
                motivo);
        incidenteTecnicoRepository.save(incidente);
    }

    // Marcar marca al técnico
    public void marcarMarca(int idTecnico, String motivo, Ticket ticket) {
        Tecnico tecnico = buscarPorId(idTecnico);
        sumarMarca(tecnico);

        if (tecnico.getMarcas() >= 3) {
            tecnico.setMarcas(0);
            motivo = motivo + " - Marcas acumuladas";
            marcarFalla(idTecnico, motivo, ticket);
        }

        IncidenteTecnico incidente = new IncidenteTecnico(tecnico, ticket, TipoIncidente.MARCA,
                motivo);
        incidenteTecnicoRepository.save(incidente);
    }

    // Tomar ticket asignándolo al técnico
    public TicketResponseDto tomarTicket(int idTecnico, int idTicket) {
        Tecnico tecnico = buscarPorId(idTecnico);
        if (tecnico.isBloqueado()) {
            throw new IllegalStateException("El técnico está bloqueado y no puede tomar tickets");
        }
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));

        if (!ticket.getEstado().equals(EstadoTicket.NO_ATENDIDO) &&
                !ticket.getEstado().equals(EstadoTicket.REABIERTO)) {
            throw new IllegalStateException("El ticket ya está siendo atendido o no está disponible");
        }

        ticket.setEstado(EstadoTicket.ATENDIDO);
        TecnicoPorTicket historial = tecnicoPorTicketService.registrarToma(ticket, tecnico);
        ticket.agregarEntradaHistorial(historial);
        ticketRepository.save(ticket);

        // Auditar asignación de ticket
        auditoriaService.registrarAccion(
                tecnico,
                AccionAuditoria.ASSIGN_TICKET,
                "TICKET",
                ticket.getId(),
                "Ticket asignado a técnico: " + tecnico.getNombre(),
                EstadoTicket.NO_ATENDIDO,
                EstadoTicket.ATENDIDO,
                CategoriaAuditoria.BUSINESS,
                SeveridadAuditoria.MEDIUM);

        return mapToTicketDto(ticket);
    }

    // Resolver ticket como técnico
    @Transactional
    public TicketResponseDto resolverTicket(int idTecnico, int idTicket) {
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));

        Tecnico tecnico = buscarPorId(idTecnico);
        if (tecnico.isBloqueado()) {
            throw new IllegalStateException("El técnico está bloqueado y no puede resolver tickets");
        }
        if (!ticket.getEstado().equals(EstadoTicket.ATENDIDO)) {
            throw new IllegalStateException("El ticket no está en estado ATENDIDO");
        }

        // LOG: Estado antes de buscar historial
        Logger logger = LoggerFactory.getLogger(TecnicoService.class);
        logger.info("[devolverTicket] Ticket id: {} estado: {}", ticket.getId(), ticket.getEstado());
        logger.info("[devolverTicket] Tecnico id: {} nombre: {}", tecnico.getId(), tecnico.getNombre());

        logger.info("[devolverTicket] Buscando historial por idTecnico={} y idTicket={}", idTecnico, idTicket);
        tecnicoPorTicketService.registrarResolucion(idTecnico, idTicket);

        // Si el ticket venía de estado REABIERTO, restar una falla al técnico si tiene
        if (ticket.getEstado().equals(EstadoTicket.ATENDIDO)) {
            List<TecnicoPorTicket> historial = ticket.getHistorialTecnicos();
            if (!historial.isEmpty()) {
                TecnicoPorTicket ultimaEntrada = historial.get(historial.size() - 1);
                if (ultimaEntrada.getEstadoInicial() == EstadoTicket.REABIERTO && tecnico.getFallas() > 0) {
                    tecnico.setFallas(tecnico.getFallas() - 1);
                    tecnicoRepository.save(tecnico);
                    // Registrar incidente técnico
                    IncidenteTecnico incidente = new IncidenteTecnico(tecnico, ticket,
                            TipoIncidente.FALLA,
                            "Falla restada por resolver ticket reabierto");
                    incidenteTecnicoRepository.save(incidente);
                }
            }
        }
        ticket.setEstado(EstadoTicket.RESUELTO);
        ticketRepository.save(ticket);

        // Auditar resolución de ticket
        auditoriaService.registrarAccion(
                tecnico,
                AccionAuditoria.RESOLVE_TICKET,
                "TICKET",
                ticket.getId(),
                "Ticket resuelto por técnico: " + tecnico.getNombre(),
                EstadoTicket.ATENDIDO,
                EstadoTicket.RESUELTO,
                CategoriaAuditoria.BUSINESS,
                SeveridadAuditoria.LOW);

        return mapToTicketDto(ticket);
    }

    // Solicitar devolución de ticket
    @Transactional
    public TicketResponseDto solicitarDevolucion(int idTecnico, int idTicket, String motivo) {
        Tecnico tecnico = buscarPorId(idTecnico);
        if (tecnico.isBloqueado()) {
            throw new IllegalStateException("El técnico está bloqueado y no puede devolver tickets");
        }
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));

        // Usa el método utilitario del modelo para obtener el técnico actual (revisar )
        Tecnico tecnicoActual = ticket.getTecnicoActual();
        if (tecnicoActual == null || tecnicoActual.getId() != tecnico.getId()) {
            throw new IllegalArgumentException("Este ticket no pertenece a este técnico");
        }

        if (!(ticket.getEstado().equals(EstadoTicket.ATENDIDO) || ticket.getEstado().equals(EstadoTicket.REABIERTO))) {
            throw new IllegalStateException("Solo se pueden devolver tickets en estado ATENDIDO o REABIERTO");
        }

        // crear la solicitud de devolución
        SolicitudDevolucion solicitud = new SolicitudDevolucion(tecnico, ticket, motivo);
        solicitudDevolucionRepository.save(solicitud);

        // Auditar solicitud de devolución
        auditoriaService.registrarAccion(
                tecnico,
                AccionAuditoria.REQUEST_RETURN,
                "TICKET",
                ticket.getId(),
                "Solicitud de devolución creada: " + motivo,
                null,
                solicitud,
                CategoriaAuditoria.BUSINESS,
                SeveridadAuditoria.MEDIUM);

        return mapToTicketDto(ticket);
    }

    // Ver solicitudes de devolución pendientes para un técnico
    public List<SolicitudDevolucion> verSolicitudesDevolucionPendientes(int idTecnico) {
        return solicitudDevolucionRepository.findByTecnicoId(idTecnico).stream()
                .filter(s -> s.getEstado() == EstadoSolicitud.PENDIENTE)
                .toList();
    }

    // Obtener historial de incidentes como DTOs
    public List<IncidenteTecnicoResponseDto> obtenerHistorialIncidentes(int idTecnico) {
        return incidenteTecnicoRepository.findByTecnicoId(idTecnico).stream()
                .map(this::mapToIncidenteDto)
                .toList();
    }

    // Listar todos los técnicos como DTOs
    public List<TecnicoResponseDto> listarTodos() {
        return tecnicoRepository.findAll().stream()
                .map(this::mapToTecnicoDto)
                .toList();
    }

    // Ver tickets asignados como DTOs
    public List<TicketResponseDto> verTicketsAsignados(int idTecnico) {
        Tecnico tecnico = buscarPorId(idTecnico);
        return tecnico.getTicketsActuales().stream()
                .filter(ticket -> ticket.getEstado() == EstadoTicket.ATENDIDO)
                .map(this::mapToTicketDto)
                .toList();
    }

    // Obtener datos del técnico
    public TecnicoResponseDto getDatosTecnico(int idTecnico) {
        Tecnico tecnico = buscarPorId(idTecnico);
        return new TecnicoResponseDto(
                tecnico.getId(),
                tecnico.getNombre(),
                tecnico.getApellido(),
                tecnico.getEmail(),
                tecnico.getRol().name(),
                tecnico.isCambiarPass(),
                tecnico.isActivo(),
                tecnico.isBloqueado(),
                tecnico.getFallas(),
                tecnico.getMarcas(),
                null // No lista de incidentes
        );
    }

    // MÉTODOS PRIVADOS/UTILIDADES
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

    // Método auxiliar para mapear Tecnico a DTO
    private TecnicoResponseDto mapToTecnicoDto(Tecnico tecnico) {
        return new TecnicoResponseDto(
                tecnico.getId(),
                tecnico.getNombre(),
                tecnico.getApellido(),
                tecnico.getEmail(),
                tecnico.getRol() != null ? tecnico.getRol().name() : null,
                tecnico.isCambiarPass(),
                tecnico.isActivo(),
                tecnico.isBloqueado(),
                tecnico.getFallas(),
                tecnico.getMarcas(),
                tecnico.getIncidentes().stream()
                        .map(this::mapToIncidenteDto)
                        .toList());
    }

    // Método auxiliar para mapear IncidenteTecnico a DTO
    private IncidenteTecnicoResponseDto mapToIncidenteDto(IncidenteTecnico incidente) {
        IncidenteTecnicoResponseDto dto = new IncidenteTecnicoResponseDto();
        dto.setIdTecnico(incidente.getTecnico().getId());
        dto.setIdTicket(incidente.getTicket().getId());
        dto.setMotivo(incidente.getMotivo());
        dto.setTipo(incidente.getTipo());
        dto.setFechaRegistro(incidente.getFechaRegistro());
        return dto;
    }
}
