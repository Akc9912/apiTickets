package com.poo.miapi.service.historial;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.historial.SolicitudDevolucion;
import com.poo.miapi.repository.historial.SolicitudDevolucionRepository;

public class SolicitudDevolucionService {
    @Autowired
    private SolicitudDevolucionRepository solicitudDevolucionRepository;
    
    // nueva solicitud de devolucion
    public SolicitudDevolucion solicitarDevolucion(Ticket ticket, Tecnico tecnico, String motivo){
        SolicitudDevolucion solicitud = new SolicitudDevolucion(tecnico,ticket,motivo);
        return solicitudDevolucionRepository.save(solicitud);
    }

    // ver todas las solicitudes desde admin
    public List<SolicitudDevolucion> verSolicitudesDevolucion() {
        return solicitudDevolucionRepository.findAll();
    }

    // mapear a DTO
}
