package com.poo.miapi.service.notificacion.motor;

import com.poo.miapi.model.notificacion.events.*;
import com.poo.miapi.service.notificacion.NotificacionService;
import com.poo.miapi.model.enums.*;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Motor de Notificaciones - Escucha eventos específicos del sistema de tickets
 * y genera notificaciones automáticamente según las reglas de negocio
 */
@Service
public class MotorNotificacionService {

    private static final Logger logger = LoggerFactory.getLogger(MotorNotificacionService.class);

    private final NotificacionService notificacionService;

    public MotorNotificacionService(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
        logger.info("Motor de Notificaciones inicializado para eventos específicos de tickets");
    }

    // LISTENERS PARA EVENTOS ESPECÍFICOS DE NEGOCIO

    /**
     * 1. Cuando un técnico marca como resuelto el ticket -> notificar al creador
     * para evaluación
     */
    @EventListener
    @Async
    public void manejarSolicitudEvaluacion(TicketEvaluacionSolicitadaEvent event) {
        try {
            Ticket ticket = event.getTicket();
            Usuario tecnico = event.getTecnico();
            String comentarioResolucion = event.getComentarioResolucion();

            logger.info("Procesando solicitud de evaluación para ticket: {} por técnico: {}",
                    ticket.getId(), tecnico.getId());

            // Notificar al creador del ticket que debe evaluar la resolución
            String titulo = "Ticket listo para evaluación";
            String mensaje = String.format(
                    "El técnico %s ha marcado como resuelto tu ticket #%d '%s'. " +
                            "Por favor revisa la solución y evalúa si el problema fue resuelto correctamente.\n\n" +
                            "Comentario del técnico: %s",
                    tecnico.getNombre(),
                    ticket.getId(),
                    ticket.getTitulo(),
                    comentarioResolucion != null ? comentarioResolucion : "Sin comentarios adicionales");

            notificacionService.crearNotificacionCompleta(
                    ticket.getCreador().getId(),
                    titulo,
                    mensaje,
                    TipoNotificacion.TICKET_ASIGNADO, // Reutilizamos el enum disponible
                    CategoriaNotificacion.TICKETS,
                    PrioridadNotificacion.ALTA,
                    SeveridadNotificacion.INFO,
                    "EVALUACION_TICKET",
                    ticket.getId(),
                    Map.of(
                            "ticketId", ticket.getId(),
                            "tecnicoId", tecnico.getId(),
                            "tecnicoNombre", tecnico.getNombre(),
                            "accion", "EVALUAR_RESOLUCION",
                            "comentarioResolucion", comentarioResolucion != null ? comentarioResolucion : ""),
                    LocalDateTime.now().plusDays(3), // Expira en 3 días
                    null);

            logger.info("Notificación de evaluación enviada al creador {} para ticket {}",
                    ticket.getCreador().getId(), ticket.getId());

        } catch (Exception e) {
            logger.error("Error procesando solicitud de evaluación para ticket {}: {}",
                    event.getTicket().getId(), e.getMessage(), e);
        }
    }

    /**
     * 2. Cuando el creador evalúa el ticket -> notificar al técnico el resultado
     */
    @EventListener
    @Async
    public void manejarEvaluacionTicket(TicketEvaluadoEvent event) {
        try {
            Ticket ticket = event.getTicket();
            Usuario creador = event.getCreador();
            Usuario tecnico = event.getTecnico();
            boolean aprobado = event.isAprobado();
            String comentarioEvaluacion = event.getComentarioEvaluacion();

            logger.info("Procesando evaluación de ticket: {} - Aprobado: {} por creador: {}",
                    ticket.getId(), aprobado, creador.getId());

            String titulo, mensaje;
            PrioridadNotificacion prioridad;
            SeveridadNotificacion severidad;

            if (aprobado) {
                titulo = "Evaluación aprobada";
                mensaje = String.format(
                        "¡Excelente trabajo! El creador del ticket #%d '%s' ha aprobado tu resolución.\n\n" +
                                "Comentario del creador: %s\n\n" +
                                "El ticket ha sido finalizado exitosamente.",
                        ticket.getId(),
                        ticket.getTitulo(),
                        comentarioEvaluacion != null ? comentarioEvaluacion : "Sin comentarios adicionales");
                prioridad = PrioridadNotificacion.MEDIA;
                severidad = SeveridadNotificacion.INFO;
            } else {
                titulo = "Evaluación rechazada - Acción requerida";
                mensaje = String.format(
                        "El creador del ticket #%d '%s' ha rechazado la resolución propuesta. " +
                                "Es necesario revisar el incidente nuevamente.\n\n" +
                                "Motivo del rechazo: %s\n\n" +
                                "Por favor revisa los comentarios y ajusta la solución según sea necesario.",
                        ticket.getId(),
                        ticket.getTitulo(),
                        comentarioEvaluacion != null ? comentarioEvaluacion : "Sin motivo específico");
                prioridad = PrioridadNotificacion.ALTA;
                severidad = SeveridadNotificacion.WARNING;
            }

            notificacionService.crearNotificacionCompleta(
                    tecnico.getId(),
                    titulo,
                    mensaje,
                    TipoNotificacion.TICKET_ASIGNADO,
                    CategoriaNotificacion.TICKETS,
                    prioridad,
                    severidad,
                    "RESULTADO_EVALUACION",
                    ticket.getId(),
                    Map.of(
                            "ticketId", ticket.getId(),
                            "creadorId", creador.getId(),
                            "aprobado", aprobado,
                            "comentarioEvaluacion", comentarioEvaluacion != null ? comentarioEvaluacion : "",
                            "accion", aprobado ? "TICKET_FINALIZADO" : "REVISAR_INCIDENTE"),
                    aprobado ? null : LocalDateTime.now().plusDays(2), // Si fue rechazado, expira en 2 días
                    null);

            logger.info("Notificación de resultado de evaluación enviada al técnico {} - Aprobado: {}",
                    tecnico.getId(), aprobado);

        } catch (Exception e) {
            logger.error("Error procesando evaluación de ticket {}: {}",
                    event.getTicket().getId(), e.getMessage(), e);
        }
    }

    /**
     * 3. Cuando un técnico solicita devolución -> notificar al admin
     */
    @EventListener
    @Async
    public void manejarSolicitudDevolucion(SolicitudDevolucionEvent event) {
        try {
            Ticket ticket = event.getTicket();
            Usuario tecnico = event.getTecnico();
            String motivoDevolucion = event.getMotivoDevolucion();

            logger.info("Procesando solicitud de devolución para ticket: {} por técnico: {}",
                    ticket.getId(), tecnico.getId());

            // Notificar a administradores sobre la solicitud de devolución
            String titulo = "Solicitud de devolución de ticket";
            String mensaje = String.format(
                    "El técnico %s ha solicitado la devolución del ticket #%d '%s'.\n\n" +
                            "Motivo de la devolución: %s\n\n" +
                            "Se requiere tu autorización para procesar esta solicitud. " +
                            "Puedes aprobar o rechazar la devolución desde el panel de administración.",
                    tecnico.getNombre(),
                    ticket.getId(),
                    ticket.getTitulo(),
                    motivoDevolucion);

            // TODO: Obtener lista de administradores dinámicamente
            // Por ahora, crear notificación genérica que se puede filtrar por rol
            notificacionService.crearNotificacionCompleta(
                    1, // ID temporal - debería ser dinámico para admins
                    titulo,
                    mensaje,
                    TipoNotificacion.TICKET_ASIGNADO,
                    CategoriaNotificacion.TICKETS,
                    PrioridadNotificacion.ALTA,
                    SeveridadNotificacion.WARNING,
                    "SOLICITUD_DEVOLUCION",
                    ticket.getId(),
                    Map.of(
                            "ticketId", ticket.getId(),
                            "tecnicoId", tecnico.getId(),
                            "tecnicoNombre", tecnico.getNombre(),
                            "motivoDevolucion", motivoDevolucion,
                            "accion", "PROCESAR_DEVOLUCION",
                            "requiereAprobacion", true),
                    LocalDateTime.now().plusDays(1), // Expira en 1 día
                    tecnico // Usando tecnico como creador temporal
            );

            logger.info("Notificación de solicitud de devolución enviada a administradores para ticket {}",
                    ticket.getId());

        } catch (Exception e) {
            logger.error("Error procesando solicitud de devolución para ticket {}: {}",
                    event.getTicket().getId(), e.getMessage(), e);
        }
    }

    /**
     * 4. Cuando admin procesa solicitud de devolución -> notificar según resultado
     */
    @EventListener
    @Async
    public void manejarDevolucionProcesada(DevolucionProcesadaEvent event) {
        try {
            Ticket ticket = event.getTicket();
            Usuario admin = event.getAdmin();
            Usuario tecnico = event.getTecnico();
            Usuario trabajador = event.getTrabajador();
            boolean aprobada = event.isAprobada();
            String comentarioAdmin = event.getComentarioAdmin();

            logger.info("Procesando resultado de devolución para ticket: {} - Aprobada: {} por admin: {}",
                    ticket.getId(), aprobada, admin.getId());

            if (aprobada) {
                // Devolución aprobada: notificar al trabajador y al técnico

                // 1. Notificar al trabajador que el técnico se desasignó
                String tituloTrabajador = "Ticket devuelto - Técnico desasignado";
                String mensajeTrabajador = String.format(
                        "El administrador %s ha aprobado la solicitud de devolución del técnico para tu ticket #%d '%s'.\n\n"
                                +
                                "El ticket está ahora pendiente de nueva asignación técnica.\n\n" +
                                "Comentario del administrador: %s",
                        admin.getNombre(),
                        ticket.getId(),
                        ticket.getTitulo(),
                        comentarioAdmin != null ? comentarioAdmin : "Sin comentarios adicionales");

                notificacionService.crearNotificacionCompleta(
                        trabajador.getId(),
                        tituloTrabajador,
                        mensajeTrabajador,
                        TipoNotificacion.TICKET_ASIGNADO,
                        CategoriaNotificacion.TICKETS,
                        PrioridadNotificacion.MEDIA,
                        SeveridadNotificacion.INFO,
                        "DEVOLUCION_APROBADA_TRABAJADOR",
                        ticket.getId(),
                        Map.of(
                                "ticketId", ticket.getId(),
                                "adminId", admin.getId(),
                                "tecnicoAnteriorId", tecnico.getId(),
                                "accion", "PENDIENTE_REASIGNACION"),
                        null,
                        null);

                // 2. Notificar al técnico que su solicitud fue aprobada
                String tituloTecnico = "Solicitud de devolución aprobada";
                String mensajeTecnico = String.format(
                        "Tu solicitud de devolución del ticket #%d '%s' ha sido aprobada por el administrador %s.\n\n" +
                                "Has sido desasignado del ticket exitosamente.\n\n" +
                                "Comentario del administrador: %s",
                        ticket.getId(),
                        ticket.getTitulo(),
                        admin.getNombre(),
                        comentarioAdmin != null ? comentarioAdmin : "Sin comentarios adicionales");

                notificacionService.crearNotificacionCompleta(
                        tecnico.getId(),
                        tituloTecnico,
                        mensajeTecnico,
                        TipoNotificacion.TICKET_ASIGNADO,
                        CategoriaNotificacion.TICKETS,
                        PrioridadNotificacion.MEDIA,
                        SeveridadNotificacion.INFO,
                        "DEVOLUCION_APROBADA_TECNICO",
                        ticket.getId(),
                        Map.of(
                                "ticketId", ticket.getId(),
                                "adminId", admin.getId(),
                                "accion", "DESASIGNADO_EXITOSAMENTE"),
                        null,
                        null);

            } else {
                // Devolución rechazada: solo notificar al técnico
                String titulo = "Solicitud de devolución rechazada";
                String mensaje = String.format(
                        "Tu solicitud de devolución del ticket #%d '%s' ha sido rechazada por el administrador %s.\n\n"
                                +
                                "Sigues asignado al ticket y debes continuar trabajando en él.\n\n" +
                                "Motivo del rechazo: %s",
                        ticket.getId(),
                        ticket.getTitulo(),
                        admin.getNombre(),
                        comentarioAdmin != null ? comentarioAdmin : "Sin motivo específico");

                notificacionService.crearNotificacionCompleta(
                        tecnico.getId(),
                        titulo,
                        mensaje,
                        TipoNotificacion.TICKET_ASIGNADO,
                        CategoriaNotificacion.TICKETS,
                        PrioridadNotificacion.ALTA,
                        SeveridadNotificacion.WARNING,
                        "DEVOLUCION_RECHAZADA",
                        ticket.getId(),
                        Map.of(
                                "ticketId", ticket.getId(),
                                "adminId", admin.getId(),
                                "comentarioRechazo", comentarioAdmin != null ? comentarioAdmin : "",
                                "accion", "CONTINUAR_ASIGNADO"),
                        LocalDateTime.now().plusDays(1), // Expira en 1 día
                        null);
            }

            logger.info("Notificaciones de devolución procesada enviadas - Ticket: {}, Aprobada: {}",
                    ticket.getId(), aprobada);

        } catch (Exception e) {
            logger.error("Error procesando devolución para ticket {}: {}",
                    event.getTicket().getId(), e.getMessage(), e);
        }
    }

    /**
     * 5. Cuando admin reabre un ticket finalizado -> notificar al trabajador y
     * último técnico
     */
    @EventListener
    @Async
    public void manejarTicketReabierto(TicketReabiertoEvent event) {
        try {
            Ticket ticket = event.getTicket();
            Usuario admin = event.getAdmin();
            Usuario trabajadorOriginal = event.getTrabajadorOriginal();
            Usuario ultimoTecnico = event.getUltimoTecnico();
            String motivoReapertura = event.getMotivoReapertura();

            logger.info("Procesando reapertura de ticket: {} por admin: {}",
                    ticket.getId(), admin.getId());

            // 1. Notificar al trabajador original
            String tituloTrabajador = "Ticket reabierto - Requiere atención";
            String mensajeTrabajador = String.format(
                    "El administrador %s ha reabierto tu ticket #%d '%s' que estaba finalizado.\n\n" +
                            "El ticket está nuevamente activo y pendiente de atención.\n\n" +
                            "Motivo de la reapertura: %s",
                    admin.getNombre(),
                    ticket.getId(),
                    ticket.getTitulo(),
                    motivoReapertura);

            notificacionService.crearNotificacionCompleta(
                    trabajadorOriginal.getId(),
                    tituloTrabajador,
                    mensajeTrabajador,
                    TipoNotificacion.TICKET_ASIGNADO,
                    CategoriaNotificacion.TICKETS,
                    PrioridadNotificacion.ALTA,
                    SeveridadNotificacion.WARNING,
                    "TICKET_REABIERTO_TRABAJADOR",
                    ticket.getId(),
                    Map.of(
                            "ticketId", ticket.getId(),
                            "adminId", admin.getId(),
                            "motivoReapertura", motivoReapertura,
                            "accion", "PENDIENTE_NUEVAMENTE"),
                    null,
                    null);

            // 2. Notificar al último técnico que trabajó en el ticket
            if (ultimoTecnico != null) {
                String tituloTecnico = "Incidente reportado - Ticket reabierto";
                String mensajeTecnico = String.format(
                        "El administrador %s ha reabierto el ticket #%d '%s' en el que trabajaste previamente.\n\n" +
                                "Se ha reportado un incidente relacionado con la resolución anterior.\n\n" +
                                "Motivo de la reapertura: %s\n\n" +
                                "Es posible que necesites revisar tu trabajo previo o proporcionar información adicional.",
                        admin.getNombre(),
                        ticket.getId(),
                        ticket.getTitulo(),
                        motivoReapertura);

                notificacionService.crearNotificacionCompleta(
                        ultimoTecnico.getId(),
                        tituloTecnico,
                        mensajeTecnico,
                        TipoNotificacion.TICKET_ASIGNADO,
                        CategoriaNotificacion.TICKETS,
                        PrioridadNotificacion.ALTA,
                        SeveridadNotificacion.WARNING,
                        "TICKET_REABIERTO_TECNICO",
                        ticket.getId(),
                        Map.of(
                                "ticketId", ticket.getId(),
                                "adminId", admin.getId(),
                                "motivoReapertura", motivoReapertura,
                                "accion", "INCIDENTE_REPORTADO"),
                        null,
                        null);
            }

            logger.info("Notificaciones de reapertura enviadas - Ticket: {}, Trabajador: {}, Técnico: {}",
                    ticket.getId(), trabajadorOriginal.getId(),
                    ultimoTecnico != null ? ultimoTecnico.getId() : "N/A");

        } catch (Exception e) {
            logger.error("Error procesando reapertura de ticket {}: {}",
                    event.getTicket().getId(), e.getMessage(), e);
        }
    }
}
