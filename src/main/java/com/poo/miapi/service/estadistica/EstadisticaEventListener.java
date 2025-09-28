package com.poo.miapi.service.estadistica;

import com.poo.miapi.model.notificacion.events.*;
import com.poo.miapi.model.core.Tecnico;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener que actualiza estadísticas cuando ocurren eventos de negocio
 * Se conecta con tu sistema de notificaciones existente
 */
@Service
public class EstadisticaEventListener {

    private static final Logger logger = LoggerFactory.getLogger(EstadisticaEventListener.class);

    private final EstadisticaActualizadorService estadisticaActualizadorService;

    public EstadisticaEventListener(EstadisticaActualizadorService estadisticaActualizadorService) {
        this.estadisticaActualizadorService = estadisticaActualizadorService;
    }

    /**
     * Cuando se resuelve un ticket → actualizar estadísticas
     */
    @EventListener
    @Async
    public void onTicketEvaluacionSolicitada(TicketEvaluacionSolicitadaEvent event) {
        try {
            logger.debug("Actualizando estadísticas por ticket resuelto: {}", event.getTicket().getId());

            // Incrementar tickets resueltos del día
            estadisticaActualizadorService.incrementarTicketsResueltos(event.getTicket());

            // Actualizar estadísticas del técnico si es un Técnico
            if (event.getTecnico() instanceof Tecnico) {
                estadisticaActualizadorService.actualizarEstadisticasTecnico(
                        (Tecnico) event.getTecnico(),
                        event.getTicket());
            }

        } catch (Exception e) {
            logger.error("Error actualizando estadísticas por ticket resuelto: {}", e.getMessage(), e);
        }
    }

    /**
     * Cuando se evalúa un ticket → actualizar métricas de calidad
     */
    @EventListener
    @Async
    public void onTicketEvaluado(TicketEvaluadoEvent event) {
        try {
            logger.debug("Actualizando estadísticas por evaluación: ticket={}, aprobado={}",
                    event.getTicket().getId(), event.isAprobado());

            // Actualizar métricas de calidad si es un Técnico
            if (event.getTecnico() instanceof Tecnico) {
                estadisticaActualizadorService.actualizarMetricasCalidad(
                        event.getTicket(),
                        (Tecnico) event.getTecnico(),
                        event.isAprobado());
            }

        } catch (Exception e) {
            logger.error("Error actualizando estadísticas por evaluación: {}", e.getMessage(), e);
        }
    }

    /**
     * Cuando se solicita devolución → actualizar métricas
     */
    @EventListener
    @Async
    public void onSolicitudDevolucion(SolicitudDevolucionEvent event) {
        try {
            logger.debug("Actualizando estadísticas por solicitud devolución: {}", event.getTicket().getId());

            if (event.getTecnico() instanceof Tecnico) {
                estadisticaActualizadorService.incrementarSolicitudesDevolucion(
                        event.getTicket(),
                        (Tecnico) event.getTecnico());
            }

        } catch (Exception e) {
            logger.error("Error actualizando estadísticas por solicitud devolución: {}", e.getMessage(), e);
        }
    }

    /**
     * Cuando se procesa devolución → actualizar métricas
     */
    @EventListener
    @Async
    public void onDevolucionProcesada(DevolucionProcesadaEvent event) {
        try {
            logger.debug("Actualizando estadísticas por devolución procesada: ticket={}, aprobada={}",
                    event.getTicket().getId(), event.isAprobada());

            if (event.getTecnico() instanceof Tecnico) {
                estadisticaActualizadorService.actualizarDevolucionProcesada(
                        event.getTicket(),
                        (Tecnico) event.getTecnico(),
                        event.isAprobada());
            }

        } catch (Exception e) {
            logger.error("Error actualizando estadísticas por devolución procesada: {}", e.getMessage(), e);
        }
    }

    /**
     * Cuando se reabre ticket → actualizar métricas
     */
    @EventListener
    @Async
    public void onTicketReabierto(TicketReabiertoEvent event) {
        try {
            logger.debug("Actualizando estadísticas por ticket reabierto: {}", event.getTicket().getId());

            estadisticaActualizadorService.incrementarTicketsReabiertos(event.getTicket());

        } catch (Exception e) {
            logger.error("Error actualizando estadísticas por ticket reabierto: {}", e.getMessage(), e);
        }
    }
}
