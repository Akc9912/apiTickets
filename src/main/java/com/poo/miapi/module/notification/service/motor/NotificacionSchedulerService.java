package com.poo.miapi.service.notificacion.motor;

import com.poo.miapi.service.notificacion.NotificacionService;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.enums.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Servicio de notificaciones programadas - Envía recordatorios y limpia
 * notificaciones expiradas
 */
@Service
public class NotificacionSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionSchedulerService.class);

    private final NotificacionService notificacionService;

    public NotificacionSchedulerService(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    /**
     * Ejecuta cada hora para enviar recordatorios de tickets pendientes
     */
    @Scheduled(fixedRate = 3600000) // 1 hora = 3,600,000 ms
    public void enviarRecordatoriosTicketsPendientes() {
        try {
            logger.info("Iniciando envío de recordatorios de tickets pendientes");

            // TODO: Implementar búsqueda de tickets sin actualizar cuando el método esté
            // disponible
            // List<Ticket> ticketsSinActualizar = ticketRepository
            // .findByEstadoAndFechaUltimaActualizacionBefore(EstadoTicket.ATENDIDO,
            // hace24Horas);

            // Por ahora placeholder vacío
            List<Ticket> ticketsSinActualizar = List.of();

            for (Ticket ticket : ticketsSinActualizar) {
                enviarRecordatorioTicketPendiente(ticket);
            }

            logger.info("Recordatorios de tickets pendientes enviados: {}", ticketsSinActualizar.size());

        } catch (Exception e) {
            logger.error("Error enviando recordatorios de tickets pendientes: {}", e.getMessage(), e);
        }
    }

    /**
     * Ejecuta cada 6 horas para limpiar notificaciones expiradas
     */
    @Scheduled(fixedRate = 21600000) // 6 horas = 21,600,000 ms
    public void limpiarNotificacionesExpiradas() {
        try {
            logger.info("Iniciando limpieza de notificaciones expiradas");

            // TODO: Implementar método archivarExpiradas cuando se agregue al repository
            // int notificacionesEliminadas =
            // notificacionRepository.archivarExpiradas(LocalDateTime.now());
            int notificacionesEliminadas = 0; // Placeholder

            logger.info("Notificaciones expiradas archivadas: {}", notificacionesEliminadas);

        } catch (Exception e) {
            logger.error("Error limpiando notificaciones expiradas: {}", e.getMessage(), e);
        }
    }

    /**
     * Ejecuta diariamente a las 9:00 AM para enviar resúmenes diarios
     */
    @Scheduled(cron = "0 0 9 * * *") // Todos los días a las 9:00 AM
    public void enviarResumenDiarioAAdministradores() {
        try {
            logger.info("Iniciando envío de resúmenes diarios");

            // Obtener estadísticas del día anterior
            // LocalDateTime inicioAyer =
            // LocalDateTime.now().minusDays(1).toLocalDate().atStartOfDay();

            // TODO: Implementar contadores cuando los métodos estén disponibles en
            // repository
            // long ticketsCreados =
            // ticketRepository.countByFechaCreacionBetween(inicioAyer,
            // inicioAyer.plusDays(1));
            // long ticketsResueltos =
            // ticketRepository.countByEstadoAndFechaUltimaActualizacionBetween(
            // EstadoTicket.RESUELTO, inicioAyer, inicioAyer.plusDays(1));
            // long ticketsPendientes = ticketRepository.countByEstadoIn(
            // List.of(EstadoTicket.NO_ATENDIDO, EstadoTicket.ATENDIDO));

            long ticketsCreados = 0; // Placeholder
            long ticketsResueltos = 0; // Placeholder
            long ticketsPendientes = 0; // Placeholder

            // TODO: Obtener lista de administradores y enviar resumen
            // Por ahora solo log
            logger.info("Resumen diario - Creados: {}, Resueltos: {}, Pendientes: {}",
                    ticketsCreados, ticketsResueltos, ticketsPendientes);

        } catch (Exception e) {
            logger.error("Error enviando resúmenes diarios: {}", e.getMessage(), e);
        }
    }

    /**
     * Ejecuta cada lunes a las 8:00 AM para envío de reportes semanales
     */
    @Scheduled(cron = "0 0 8 * * MON") // Todos los lunes a las 8:00 AM
    public void enviarReportesSemanalAAdministradores() {
        try {
            logger.info("Iniciando envío de reportes semanales");

            // Obtener estadísticas de la semana pasada
            LocalDateTime inicioSemanaAnterior = LocalDateTime.now().minusWeeks(1).toLocalDate().atStartOfDay();
            LocalDateTime finSemanaAnterior = inicioSemanaAnterior.plusWeeks(1);

            // TODO: Implementar generación de reportes semanales
            logger.info("Reporte semanal generado para período: {} a {}",
                    inicioSemanaAnterior, finSemanaAnterior);

        } catch (Exception e) {
            logger.error("Error enviando reportes semanales: {}", e.getMessage(), e);
        }
    }

    /**
     * Ejecuta cada 30 minutos para verificar tickets críticos sin asignar
     */
    @Scheduled(fixedRate = 1800000) // 30 minutos = 1,800,000 ms
    public void verificarTicketsCriticosSinAsignar() {
        try {
            logger.debug("Verificando tickets críticos sin asignar");

            // TODO: Implementar búsqueda de tickets críticos cuando esté disponible el
            // campo prioridad
            // List<Ticket> ticketsCriticos = ticketRepository
            // .findByPrioridadAndTecnicoAsignadoIsNullAndEstadoIn(
            // PrioridadTicket.CRITICA,
            // List.of(EstadoTicket.NO_ATENDIDO, EstadoTicket.ATENDIDO)
            // );

            // Por ahora solo log de que el método se ejecuta
            logger.debug("Verificación de tickets críticos completada");

        } catch (Exception e) {
            logger.error("Error verificando tickets críticos sin asignar: {}", e.getMessage(), e);
        }
    }

    // MÉTODOS PRIVADOS

    private void enviarRecordatorioTicketPendiente(Ticket ticket) {
        try {
            Tecnico tecnicoActual = ticket.getTecnicoActual();
            if (tecnicoActual != null) {
                String titulo = "Recordatorio: Ticket pendiente";
                String mensaje = String.format(
                        "El ticket #%d '%s' lleva más de 24 horas sin actualización. " +
                                "Por favor, revisa el estado y actualiza si es necesario.",
                        ticket.getId(), ticket.getTitulo());

                notificacionService.crearNotificacionCompleta(
                        tecnicoActual.getId(),
                        titulo,
                        mensaje,
                        TipoNotificacion.RECORDATORIO,
                        CategoriaNotificacion.TICKETS,
                        PrioridadNotificacion.MEDIA,
                        SeveridadNotificacion.WARNING,
                        "TICKET",
                        ticket.getId(),
                        Map.of(
                                "ticketId", ticket.getId(),
                                "tipoRecordatorio", "TICKET_PENDIENTE",
                                "horasSinActualizar",
                                java.time.Duration.between(ticket.getFechaUltimaActualizacion(), LocalDateTime.now())
                                        .toHours()),
                        LocalDateTime.now().plusDays(1), // Expira en 1 día
                        null // Sistema genera la notificación
                );

                logger.debug("Recordatorio enviado para ticket pendiente: {}", ticket.getId());
            }
        } catch (Exception e) {
            logger.error("Error enviando recordatorio para ticket {}: {}", ticket.getId(), e.getMessage());
        }
    }
}
