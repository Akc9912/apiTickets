package com.poo.miapi.controller;

import com.poo.miapi.dto.EvaluarTicketDto;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.service.TrabajadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ControllerAdvice
@RequestMapping("/api/trabajador")
public class TrabajadorController {

    /*
     * POST /api/trabajador/tickets
     * Descripción: Crear un nuevo ticket.
     * GET /api/trabajador/tickets
     * Descripción: Ver todos mis tickets.
     * GET /api/trabajador/tickets/activos
     * Descripción: Ver mis tickets activos.
     * POST /api/trabajador/tickets/{ticketId}/evaluar
     * Descripción: Evaluar la atención recibida en un ticket.
     */
}
