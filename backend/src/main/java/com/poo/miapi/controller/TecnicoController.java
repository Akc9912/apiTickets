package com.poo.miapi.controller;

import com.poo.miapi.model.*;
import com.poo.miapi.service.GestorDeTickets;
import com.poo.miapi.service.GestorDeUsuarios;
import com.poo.miapi.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tecnico")
public class TecnicoController {

    @Autowired
    private GestorDeUsuarios gestorUsuarios;

    @Autowired
    private GestorDeTickets gestorTickets;

    @Autowired
    private NotificacionService notificacionService;

    @PutMapping("/tomar-ticket/{idTicket}")
    public String tomarTicket(@PathVariable int idTicket,
            @RequestParam int idTecnico) {

        Usuario u = gestorUsuarios.buscarPorId(idTecnico);
        Ticket t = gestorTickets.buscarPorId(idTicket);

        if (!(u instanceof Tecnico tecnico) || t == null) {
            return "Datos inválidos.";
        }

        tecnico.tomarTicket(t);
        notificacionService.notificarTicketTomado(tecnico, t);
        return "Ticket tomado.";
    }

    @PutMapping("/resolver-ticket/{idTicket}")
    public String resolverTicket(@PathVariable int idTicket,
            @RequestParam int idTecnico) {

        Usuario u = gestorUsuarios.buscarPorId(idTecnico);
        Ticket t = gestorTickets.buscarPorId(idTicket);

        if (!(u instanceof Tecnico tecnico) || t == null) {
            return "Datos inválidos.";
        }

        tecnico.resolverTicket(t);
        notificacionService.notificarTicketResuelto(tecnico, t);
        return "Ticket resuelto.";
    }

    @PutMapping("/devolver-ticket/{idTicket}")
    public String devolverTicket(@PathVariable int idTicket,
            @RequestParam int idTecnico) {

        Usuario u = gestorUsuarios.buscarPorId(idTecnico);
        Ticket t = gestorTickets.buscarPorId(idTicket);

        if (!(u instanceof Tecnico tecnico) || t == null) {
            return "Datos inválidos.";
        }

        tecnico.devolverTicket(t);
        notificacionService.notificarTicketDevuelto(tecnico, t);
        return "Ticket devuelto.";
    }
}
