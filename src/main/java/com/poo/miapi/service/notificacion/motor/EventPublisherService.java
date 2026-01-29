package com.poo.miapi.service.notificacion.motor;

import com.poo.miapi.model.events.*;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.model.enums.EstadoTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Servicio para publicar eventos del sistema que disparan notificaciones
 * automáticas
 */
@Service
public class EventPublisherService {

    private static final Logger logger = LoggerFactory.getLogger(EventPublisherService.class);

    private final ApplicationEventPublisher eventPublisher;

    public EventPublisherService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Publica evento cuando se crea un ticket
     */
    public void publicarTicketCreado(Ticket ticket, Usuario creador, String observaciones) {
        try {
            TicketCreadoEvent event = new TicketCreadoEvent(this, ticket, creador, observaciones);
            eventPublisher.publishEvent(event);

            logger.debug("Evento TicketCreado publicado para ticket: {} por usuario: {}",
                    ticket.getId(), creador.getId());
        } catch (Exception e) {
            logger.error("Error publicando evento TicketCreado: {}", e.getMessage(), e);
        }
    }

    /**
     * Publica evento cuando se asigna un ticket
     */
    public void publicarTicketAsignado(Ticket ticket, Usuario tecnico, Usuario asignadoPor, String motivo) {
        try {
            TicketAsignadoEvent event = new TicketAsignadoEvent(this, ticket, tecnico, asignadoPor, motivo);
            eventPublisher.publishEvent(event);

            logger.debug("Evento TicketAsignado publicado para ticket: {} asignado a técnico: {}",
                    ticket.getId(), tecnico.getId());
        } catch (Exception e) {
            logger.error("Error publicando evento TicketAsignado: {}", e.getMessage(), e);
        }
    }

    /**
     * Publica evento cuando cambia el estado de un ticket
     */
    public void publicarCambioEstadoTicket(Ticket ticket, EstadoTicket estadoAnterior,
            EstadoTicket estadoNuevo, Usuario cambiadoPor, String observaciones) {
        try {
            TicketEstadoCambiadoEvent event = new TicketEstadoCambiadoEvent(
                    this, ticket, estadoAnterior, estadoNuevo, cambiadoPor, observaciones);
            eventPublisher.publishEvent(event);

            logger.debug("Evento TicketEstadoCambiado publicado para ticket: {} de {} a {}",
                    ticket.getId(), estadoAnterior, estadoNuevo);
        } catch (Exception e) {
            logger.error("Error publicando evento TicketEstadoCambiado: {}", e.getMessage(), e);
        }
    }

    /**
     * Publica evento cuando se registra una marca
     */
    public void publicarMarcaRegistrada(Usuario usuario, Usuario registradoPor, String motivo,
            String tipoMarca, Object entidadRelacionada) {
        try {
            MarcaRegistradaEvent event = new MarcaRegistradaEvent(
                    this, usuario, registradoPor, motivo, tipoMarca, entidadRelacionada);
            eventPublisher.publishEvent(event);

            logger.debug("Evento MarcaRegistrada publicado para usuario: {} por: {}",
                    usuario.getId(), registradoPor.getId());
        } catch (Exception e) {
            logger.error("Error publicando evento MarcaRegistrada: {}", e.getMessage(), e);
        }
    }

    /**
     * Publica evento cuando se crea un usuario
     */
    public void publicarUsuarioCreado(Usuario usuario, Usuario creadoPor, String passwordTemporal) {
        try {
            UsuarioCreadoEvent event = new UsuarioCreadoEvent(this, usuario, creadoPor, passwordTemporal);
            eventPublisher.publishEvent(event);

            logger.debug("Evento UsuarioCreado publicado para usuario: {} por: {}",
                    usuario.getId(), creadoPor.getId());
        } catch (Exception e) {
            logger.error("Error publicando evento UsuarioCreado: {}", e.getMessage(), e);
        }
    }

    /**
     * Publica evento cuando un técnico marca un ticket como resuelto (solicita
     * evaluación)
     */
    public void publicarTicketEvaluacionSolicitada(Ticket ticket, Usuario tecnico, String comentarioResolucion) {
        try {
            TicketEvaluacionSolicitadaEvent event = new TicketEvaluacionSolicitadaEvent(this, ticket, tecnico,
                    comentarioResolucion);
            eventPublisher.publishEvent(event);

            logger.debug("Evento TicketEvaluacionSolicitada publicado para ticket: {} por técnico: {}",
                    ticket.getId(), tecnico.getId());
        } catch (Exception e) {
            logger.error("Error publicando evento TicketEvaluacionSolicitada: {}", e.getMessage(), e);
        }
    }

    /**
     * Publica evento cuando el creador evalúa un ticket (aprueba/rechaza)
     */
    public void publicarTicketEvaluado(Ticket ticket, Usuario creador, Usuario tecnico, boolean aprobado,
            String comentarioEvaluacion) {
        try {
            TicketEvaluadoEvent event = new TicketEvaluadoEvent(this, ticket, creador, tecnico, aprobado,
                    comentarioEvaluacion);
            eventPublisher.publishEvent(event);

            logger.debug("Evento TicketEvaluado publicado para ticket: {} - Aprobado: {} por creador: {}",
                    ticket.getId(), aprobado, creador.getId());
        } catch (Exception e) {
            logger.error("Error publicando evento TicketEvaluado: {}", e.getMessage(), e);
        }
    }

    /**
     * Publica evento cuando un técnico solicita devolución de un ticket
     */
    public void publicarSolicitudDevolucion(Ticket ticket, Usuario tecnico, String motivoDevolucion) {
        try {
            SolicitudDevolucionEvent event = new SolicitudDevolucionEvent(this, ticket, tecnico, motivoDevolucion);
            eventPublisher.publishEvent(event);

            logger.debug("Evento SolicitudDevolucion publicado para ticket: {} por técnico: {}",
                    ticket.getId(), tecnico.getId());
        } catch (Exception e) {
            logger.error("Error publicando evento SolicitudDevolucion: {}", e.getMessage(), e);
        }
    }

    /**
     * Publica evento cuando un admin procesa una solicitud de devolución
     */
    public void publicarDevolucionProcesada(Ticket ticket, Usuario admin, Usuario tecnico, Usuario trabajador,
            boolean aprobada, String comentarioAdmin) {
        try {
            DevolucionProcesadaEvent event = new DevolucionProcesadaEvent(this, ticket, admin, tecnico, trabajador,
                    aprobada, comentarioAdmin);
            eventPublisher.publishEvent(event);

            logger.debug("Evento DevolucionProcesada publicado para ticket: {} - Aprobada: {} por admin: {}",
                    ticket.getId(), aprobada, admin.getId());
        } catch (Exception e) {
            logger.error("Error publicando evento DevolucionProcesada: {}", e.getMessage(), e);
        }
    }

    /**
     * Publica evento cuando un admin reabre un ticket finalizado
     */
    public void publicarTicketReabierto(Ticket ticket, Usuario admin, Usuario trabajadorOriginal,
            Usuario ultimoTecnico, String motivoReapertura) {
        try {
            TicketReabiertoEvent event = new TicketReabiertoEvent(this, ticket, admin, trabajadorOriginal,
                    ultimoTecnico, motivoReapertura);
            eventPublisher.publishEvent(event);

            logger.debug("Evento TicketReabierto publicado para ticket: {} por admin: {}",
                    ticket.getId(), admin.getId());
        } catch (Exception e) {
            logger.error("Error publicando evento TicketReabierto: {}", e.getMessage(), e);
        }
    }

    /**
     * Publica evento personalizado para notificaciones especiales del sistema
     */
    public void publicarEventoPersonalizado(String tipoEvento, Object datos, Usuario usuario) {
        try {
            // Crear un evento genérico para casos especiales
            logger.info("Publicando evento personalizado: {} para usuario: {}", tipoEvento,
                    usuario != null ? usuario.getId() : "sistema");

            // Aquí se pueden agregar más tipos de eventos según las necesidades del negocio

        } catch (Exception e) {
            logger.error("Error publicando evento personalizado {}: {}", tipoEvento, e.getMessage(), e);
        }
    }
}
