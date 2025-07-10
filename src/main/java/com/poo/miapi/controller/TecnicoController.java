package com.poo.miapi.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@ControllerAdvice
@RequestMapping("/api/tecnico")
public class TecnicoController {

    /*
     * GET /api/tecnico/tickets/asignados
     * Descripción: Ver tickets asignados a mí.
     * POST /api/tecnico/tickets/{ticketId}/tomar
     * Descripción: Tomar un ticket pendiente.
     * POST /api/tecnico/tickets/{ticketId}/resolver
     * Descripción: Marcar ticket como resuelto.
     * POST /api/tecnico/tickets/{ticketId}/devolver
     * Descripción: Devolver ticket al pool (no puedo resolverlo).
     */
}
