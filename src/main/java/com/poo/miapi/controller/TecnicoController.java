package com.poo.miapi.controller;

import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.service.TecnicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
