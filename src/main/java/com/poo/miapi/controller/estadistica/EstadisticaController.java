package com.poo.miapi.controller.estadistica;

import com.poo.miapi.model.core.EstadoTicket;
import com.poo.miapi.service.estadistica.EstadisticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticaController {

    @Autowired
    private final EstadisticaService estadisticaService;

    public EstadisticaController(EstadisticaService estadisticaService) {
        this.estadisticaService = estadisticaService;
    }

    // GET /api/estadisticas/tickets/total
    @GetMapping("/tickets/total")
    public long cantidadTotalTickets() {
        return estadisticaService.cantidadTotalTickets();
    }

    // GET /api/estadisticas/tickets/estado?estado=NO_ATENDIDO
    @GetMapping("/tickets/estado")
    public long cantidadTicketsPorEstado(@RequestParam EstadoTicket estado) {
        return estadisticaService.cantidadTicketsPorEstado(estado);
    }

    // GET /api/estadisticas/usuarios/total
    @GetMapping("/usuarios/total")
    public long cantidadTotalUsuarios() {
        return estadisticaService.cantidadTotalUsuarios();
    }

    // GET /api/estadisticas/tecnicos/total
    @GetMapping("/tecnicos/total")
    public long cantidadTotalTecnicos() {
        return estadisticaService.cantidadTotalTecnicos();
    }

    // GET /api/estadisticas/trabajadores/total
    @GetMapping("/trabajadores/total")
    public long cantidadTotalTrabajadores() {
        return estadisticaService.cantidadTotalTrabajadores();
    }

    // GET /api/estadisticas/incidentes/total
    @GetMapping("/incidentes/total")
    public long cantidadIncidentesTecnicos() {
        return estadisticaService.cantidadIncidentesTecnicos();
    }
}
