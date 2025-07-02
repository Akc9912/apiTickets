package com.poo.miapi.controller;

import com.poo.miapi.model.*;
import com.poo.miapi.service.GestorDeTickets;
import com.poo.miapi.service.GestorDeUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private GestorDeTickets gestorTickets;

    @Autowired
    private GestorDeUsuarios gestorUsuarios;

    @GetMapping("/tickets-por-estado")
    public Map<EstadoTicket, Integer> ticketsPorEstado() {
        Map<EstadoTicket, Integer> conteo = new EnumMap<>(EstadoTicket.class);
        for (EstadoTicket estado : EstadoTicket.values()) {
            int cantidad = (int) gestorTickets.obtenerTodos().stream()
                    .filter(t -> t.getEstado() == estado)
                    .count();
            conteo.put(estado, cantidad);
        }
        return conteo;
    }

    @GetMapping("/tickets-reabiertos")
    public List<Ticket> ticketsReabiertos() {
        return gestorTickets.filtrarPorEstado(EstadoTicket.REABIERTO);
    }

    @GetMapping("/fallas-por-tecnico")
    public Map<String, Integer> fallasPorTecnico() {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        for (Usuario u : gestorUsuarios.getUsuarios()) {
            if (u instanceof Tecnico tecnico) {
                resultado.put(tecnico.getNombre() + " (ID: " + tecnico.getId() + ")", tecnico.getFallas());
            }
        }
        return resultado;
    }
}