package com.poo.miapi.controller;

import com.poo.miapi.model.*;
import com.poo.miapi.service.GestorDeTickets;
import com.poo.miapi.service.GestorDeUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trabajador")
public class TrabajadorController {

    @Autowired
    private GestorDeUsuarios gestorUsuarios;

    @Autowired
    private GestorDeTickets gestorTickets;

    @PostMapping("/crear-ticket")
    public Ticket crearTicket(@RequestParam int idTrabajador,
            @RequestParam String titulo,
            @RequestParam String descripcion) {

        Usuario u = gestorUsuarios.buscarPorId(idTrabajador);
        if (!(u instanceof Trabajador trabajador)) {
            throw new RuntimeException("Trabajador no válido.");
        }

        Ticket nuevo = trabajador.crearTicket(titulo, descripcion);
        gestorTickets.registrarTicket(nuevo);
        return nuevo;
    }

    @PutMapping("/confirmar-resolucion/{idTicket}")
    public String confirmarResolucion(@PathVariable int idTicket,
            @RequestParam int idTrabajador,
            @RequestParam boolean fueResuelto) {

        Usuario u = gestorUsuarios.buscarPorId(idTrabajador);
        Ticket t = gestorTickets.buscarPorId(idTicket);

        if (!(u instanceof Trabajador trabajador) || t == null) {
            return "Datos inválidos.";
        }

        trabajador.confirmarResolucion(t, fueResuelto);
        return fueResuelto ? "Ticket finalizado." : "Ticket reabierto.";
    }
}
