package com.poo.miapi.controller;

import com.poo.miapi.model.*;
import com.poo.miapi.service.gestorDeTickets;
import com.poo.miapi.service.gestorDeUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private gestorDeTickets gestorTickets;

    @Autowired
    private gestorDeUsuarios gestorUsuarios;

    // -------------------- Trabajador --------------------

    @PostMapping("/crear")
    public Ticket crearTicket(@RequestParam int idTrabajador,
            @RequestParam String titulo,
            @RequestParam String descripcion) {

        Usuario u = gestorUsuarios.buscarPorId(idTrabajador);
        if (u == null || !(u instanceof Trabajador trabajador)) {
            throw new RuntimeException("Trabajador no válido.");
        }

        Ticket nuevo = trabajador.crearTicket(titulo, descripcion);
        gestorTickets.registrarTicket(nuevo);
        return nuevo;
    }

    @PutMapping("/{idTicket}/confirmar-resolucion")
    public String confirmarResolucion(@PathVariable int idTicket,
            @RequestParam int idTrabajador,
            @RequestParam boolean fueResuelto) {

        Usuario u = gestorUsuarios.buscarPorId(idTrabajador);
        Ticket t = gestorTickets.buscarPorId(idTicket);

        if (u == null || !(u instanceof Trabajador trabajador) || t == null) {
            return "Datos inválidos.";
        }

        trabajador.confirmarResolucion(t, fueResuelto);
        return fueResuelto ? "Ticket finalizado." : "Ticket reabierto.";
    }

    // -------------------- Técnico --------------------

    @PutMapping("/{idTicket}/tomar")
    public String tomarTicket(@PathVariable int idTicket,
            @RequestParam int idTecnico) {

        Usuario u = gestorUsuarios.buscarPorId(idTecnico);
        Ticket t = gestorTickets.buscarPorId(idTicket);

        if (u == null || !(u instanceof Tecnico tecnico) || t == null) {
            return "Datos inválidos.";
        }

        tecnico.tomarTicket(t);
        return "Ticket tomado.";
    }

    @PutMapping("/{idTicket}/resolver")
    public String resolverTicket(@PathVariable int idTicket,
            @RequestParam int idTecnico) {

        Usuario u = gestorUsuarios.buscarPorId(idTecnico);
        Ticket t = gestorTickets.buscarPorId(idTicket);

        if (u == null || !(u instanceof Tecnico tecnico) || t == null) {
            return "Datos inválidos.";
        }

        tecnico.resolverTicket(t);
        return "Ticket resuelto.";
    }

    @PutMapping("/{idTicket}/devolver")
    public String devolverTicket(@PathVariable int idTicket,
            @RequestParam int idTecnico) {

        Usuario u = gestorUsuarios.buscarPorId(idTecnico);
        Ticket t = gestorTickets.buscarPorId(idTicket);

        if (u == null || !(u instanceof Tecnico tecnico) || t == null) {
            return "Datos inválidos.";
        }

        tecnico.devolverTicket(t);
        return "Ticket devuelto.";
    }

    // -------------------- Consultas generales --------------------

    @GetMapping
    public List<Ticket> getTodos() {
        return gestorTickets.obtenerTodos();
    }

    @GetMapping("/estado")
    public List<Ticket> getPorEstado(@RequestParam EstadoTicket estado) {
        return gestorTickets.filtrarPorEstado(estado);
    }

    @GetMapping("/{id}")
    public Ticket getPorId(@PathVariable int id) {
        return gestorTickets.buscarPorId(id);
    }
}
