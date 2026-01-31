package com.poo.miapi.service.estadistica;

import com.poo.miapi.model.events.*;
import com.poo.miapi.model.core.Tecnico;
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

    /**
     * Cuando se crea un ticket → actualizar métricas de creación
     */
    @EventListener
    @Async
    public void onTicketCreado(TicketCreadoEvent event) {
        try {
            logger.debug("Actualizando estadísticas por ticket creado: {}", event.getTicket().getId());

            estadisticaActualizadorService.incrementarTicketsCreados(
                    event.getTicket(),
                    event.getCreador());

        } catch (Exception e) {
            logger.error("Error actualizando estadísticas por creación de ticket: {}", e.getMessage(), e);
        }
    }

    /**
     * Cuando se asigna un ticket → actualizar métricas de asignación
     */
    @EventListener
    @Async
    public void onTicketAsignado(TicketAsignadoEvent event) {
        try {
            logger.debug("Actualizando estadísticas por ticket asignado: {} → técnico: {}",
                    event.getTicket().getId(), event.getTecnico().getId());

            // Verificar que el técnico sea realmente un Técnico
            if (event.getTecnico() instanceof Tecnico) {
                estadisticaActualizadorService.incrementarTicketsAsignados(
                        event.getTicket(),
                        (Tecnico) event.getTecnico(),
                        event.getAsignadoPor());
            }

        } catch (Exception e) {
            logger.error("Error actualizando estadísticas por asignación de ticket: {}", e.getMessage(), e);
        }
    }

    /**
     * Cuando un usuario inicia sesión → actualizar métricas de actividad
     */
    @EventListener
    @Async
    public void onUsuarioLogin(UsuarioLoginEvent event) {
        try {
            logger.debug("Actualizando estadísticas por login: usuario={}, primeraVez={}",
                    event.getUsuario().getEmail(), event.isEsPrimerLoginDelDia());

            estadisticaActualizadorService.registrarInicioSesion(event.getUsuario());

        } catch (Exception e) {
            logger.error("Error actualizando estadísticas por login: {}", e.getMessage(), e);
        }
    }

    /**
     * Cuando un usuario cierra sesión → actualizar métricas de actividad
     */
    @EventListener
    @Async
    public void onUsuarioLogout(UsuarioLogoutEvent event) {
        try {
            logger.debug("Actualizando estadísticas por logout: usuario={}, duracion={}m",
                    event.getUsuario().getEmail(), event.getTiempoSesionMinutos());

            estadisticaActualizadorService.registrarCierreSesion(
                    event.getUsuario(),
                    event.getTiempoSesionMinutos());

        } catch (Exception e) {
            logger.error("Error actualizando estadísticas por logout: {}", e.getMessage(), e);
        }
    }

    /**
     * Cuando se crea un usuario → actualizar métricas de gestión
     */
    @EventListener
    @Async
    public void onUsuarioCreado(UsuarioCreadoEvent event) {
        try {
            logger.debug("Actualizando estadísticas por usuario creado: {} por {}",
                    event.getUsuario().getEmail(),
                    event.getCreadoPor().getEmail());

            estadisticaActualizadorService.incrementarUsuariosGestionados(
                    event.getCreadoPor(),
                    event.getUsuario());

        } catch (Exception e) {
            logger.error("Error actualizando estadísticas por creación de usuario: {}", e.getMessage(), e);
        }
    }
}
