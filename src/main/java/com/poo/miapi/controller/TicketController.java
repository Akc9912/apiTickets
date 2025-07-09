package com.poo.miapi.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@ControllerAdvice
@RequestMapping("/api/tickets")
public class TicketController {

    /*
     * GET /api/tickets
     * Descripción: Listar todos los tickets (según permisos).
     * GET /api/tickets/{id}
     * Descripción: Ver detalle de un ticket.
     * POST /api/tickets/filtrar
     * Descripción: Filtrar tickets por estado, usuario, fecha, etc
     */
}
