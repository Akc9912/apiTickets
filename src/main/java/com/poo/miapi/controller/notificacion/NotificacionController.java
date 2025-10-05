package com.poo.miapi.controller.notificacion;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones", description = "Endpoints para gestión del sistema de notificaciones")
public class NotificacionController {

        /*
         * // CRUD REST
         *
         * POST /api/notificaciones → crear nueva notificación
         *
         * GET /api/notificaciones/{id} → obtener notificación por ID
         *
         * PUT /api/notificaciones/{id} → actualizar notificación
         *
         * DELETE /api/notificaciones/{id} → eliminar notificación
         *
         * // Consultas por usuario
         *
         * GET /api/notificaciones/usuario/{usuarioId} → listar todas las notificaciones
         * del usuario
         *
         * GET /api/notificaciones/usuario/{usuarioId}/no-leidas → listar solo las no
         * leídas
         *
         * GET /api/notificaciones/usuario/{usuarioId}/contador → devolver cantidad de
         * no leídas
         *
         * PUT /api/notificaciones/{id}/leer → marcar una como leída
         *
         * PUT /api/notificaciones/usuario/{usuarioId}/leer-todas → marcar todas como
         * leídas
         *
         * DELETE /api/notificaciones/usuario/{usuarioId} → eliminar todas las
         * notificaciones del usuario
         *
         * // WebSocket / Eventos
         *
         * /ws/notificaciones → canal principal para recibir notificaciones en tiempo
         * real
         *
         * (Opcional) /ws/notificaciones/{usuarioId} → canal individual por usuario
         *
         * (Opcional) POST /api/notificaciones/enviar → enviar notificación manualmente
         * (útil para testing o administración)
         *
         * // Administración / Mantenimiento
         *
         * GET /api/notificaciones/buscar → búsqueda avanzada con filtros (tipo, rango
         * de fechas, estado, etc.)
         *
         * DELETE /api/notificaciones/expiradas → limpieza automática de notificaciones
         * antiguas
         */
}
