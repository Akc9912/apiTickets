package com.poo.miapi.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
@RequestMapping("/api/notificaciones")
public class NotificacionController {
    /*
     * GET /api/notificaciones
     * Descripción: Ver mis notificaciones.
     * DELETE /api/notificaciones
     * Descripción: Eliminar todas mis notificaciones.
     * POST /api/notificaciones
     * Descripción: (Admin) Enviar notificación a usuario(s).
     */
}
