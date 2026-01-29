package com.poo.miapi.service.historial;

import com.poo.miapi.dto.historial.SolicitudDevolucionResponseDto;
import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.enums.EstadoSolicitud;
import com.poo.miapi.model.enums.EstadoTicket;
import com.poo.miapi.model.historial.SolicitudDevolucion;
import com.poo.miapi.model.historial.TecnicoPorTicket;
import com.poo.miapi.repository.historial.SolicitudDevolucionRepository;
import com.poo.miapi.repository.historial.TecnicoPorTicketRepository;
import com.poo.miapi.service.core.TecnicoService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import com.poo.miapi.service.auditoria.AuditoriaService;
import com.poo.miapi.model.enums.AccionAuditoria;
import com.poo.miapi.model.enums.CategoriaAuditoria;
import com.poo.miapi.model.enums.SeveridadAuditoria;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitudDevolucionService {

    private final SolicitudDevolucionRepository solicitudDevolucionRepository;
    private final TecnicoService tecnicoService;
    private final TecnicoPorTicketRepository tecnicoPorTicketRepository;
    private final AuditoriaService auditoriaService;

    public SolicitudDevolucionService(SolicitudDevolucionRepository solicitudDevolucionRepository,
            TecnicoService tecnicoService, TecnicoPorTicketRepository tecnicoPorTicketRepository,
            AuditoriaService auditoriaService) {
        this.solicitudDevolucionRepository = solicitudDevolucionRepository;
        this.tecnicoService = tecnicoService;
        this.tecnicoPorTicketRepository = tecnicoPorTicketRepository;
        this.auditoriaService = auditoriaService;
    }

    // MÉTODOS PÚBLICOS
    // Crear solicitud de devolución
    public SolicitudDevolucion solicitarDevolucion(Ticket ticket, Tecnico tecnico, String motivo) {
        SolicitudDevolucion solicitud = new SolicitudDevolucion(tecnico, ticket, motivo);
        return solicitudDevolucionRepository.save(solicitud);
    }

    // Listar todas las solicitudes (para admin)
    public List<SolicitudDevolucion> verSolicitudesDevolucion() {
        return solicitudDevolucionRepository.findAll();
    }

    // Procesar solicitud de devolución (aprobar o rechazar)
    public SolicitudDevolucionResponseDto procesarSolicitudDevolucion(
            int solicitudId, int idTecnico, boolean aprobar, String comentario) {

        SolicitudDevolucion solicitud = solicitudDevolucionRepository.findById(solicitudId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        validarTecnico(solicitud, idTecnico);
        validarEstadoPendiente(solicitud);

        if (aprobar) {
            // Buscar historial de ticket por técnico para desasignar
            TecnicoPorTicket historial = tecnicoPorTicketRepository
                    .findByTecnicoAndTicket(solicitud.getTecnico(), solicitud.getTicket())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "No se encontró el historial del técnico para este ticket"));
            historial.setFechaDesasignacion(LocalDateTime.now());
            historial.setEstadoFinal(EstadoTicket.REABIERTO);
            tecnicoPorTicketRepository.save(historial);

            // Sumar marca y registrar incidente técnico usando el service de técnico
            tecnicoService.marcarMarca(solicitud.getTecnico().getId(), "Marca por devolución de ticket: " + comentario,
                    solicitud.getTicket());

            solicitud.setEstado(EstadoSolicitud.APROBADO);
        } else {
            solicitud.setEstado(EstadoSolicitud.RECHAZADO);
        }
        solicitud.setComentarioResolucion(comentario);
        solicitud.setFechaResolucion(LocalDateTime.now());

        solicitudDevolucionRepository.save(solicitud);

        // Auditar procesamiento de solicitud de devolución
        AccionAuditoria accion = aprobar ? AccionAuditoria.APPROVE_RETURN : AccionAuditoria.REJECT_RETURN;
        auditoriaService.registrarAccion(
                solicitud.getTecnico(),
                accion,
                "SOLICITUD_DEVOLUCION",
                solicitud.getId(),
                "Solicitud de devolución " + (aprobar ? "aprobada" : "rechazada") + ": " + comentario,
                EstadoSolicitud.PENDIENTE,
                solicitud.getEstado(),
                CategoriaAuditoria.BUSINESS,
                SeveridadAuditoria.MEDIUM);

        return this.toDto(solicitud);
    }

    // MÉTODOS PRIVADOS/UTILIDADES
    // Mapear entidad a DTO
    public SolicitudDevolucionResponseDto toDto(SolicitudDevolucion solicitud) {
        SolicitudDevolucionResponseDto dto = new SolicitudDevolucionResponseDto();
        dto.setId(solicitud.getId());
        dto.setIdTecnico(solicitud.getTecnico() != null ? solicitud.getTecnico().getId() : 0);
        dto.setIdTicket(solicitud.getTicket() != null ? solicitud.getTicket().getId() : 0);
        dto.setMotivo(solicitud.getMotivo());
        dto.setEstado(solicitud.getEstado() != null ? solicitud.getEstado().name() : null);
        dto.setFechaSolicitud(solicitud.getFechaSolicitud());
        dto.setFechaResolucion(solicitud.getFechaResolucion());
        dto.setIdAdminResolutor(solicitud.getAdminResolutor() != null ? solicitud.getAdminResolutor().getId() : 0);
        dto.setComentarioResolucion(solicitud.getComentarioResolucion());
        return dto;
    }

    // Validar que el técnico puede procesar la solicitud
    private void validarTecnico(SolicitudDevolucion solicitud, int idTecnico) {
        if (solicitud.getTecnico() == null || solicitud.getTecnico().getId() != idTecnico) {
            throw new SecurityException("No puedes procesar solicitudes que no te pertenecen");
        }
    }

    // Validar que la solicitud esté pendiente
    private void validarEstadoPendiente(SolicitudDevolucion solicitud) {
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException("La solicitud ya fue procesada");
        }
    }
}
