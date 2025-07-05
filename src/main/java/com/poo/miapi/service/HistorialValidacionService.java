package com.poo.miapi.service;

import com.poo.miapi.model.historial.HistorialValidacionTrabajador;
import com.poo.miapi.repository.HistorialValidacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistorialValidacionService {

    @Autowired
    private HistorialValidacionRepository historialValidacionRepository;

    public void registrarValidacion(HistorialValidacionTrabajador validacion) {
        historialValidacionRepository.save(validacion);
    }

    public List<HistorialValidacionTrabajador> listarPorTrabajador(int trabajadorId) {
        return historialValidacionRepository.findByTrabajadorId(trabajadorId);
    }

    public List<HistorialValidacionTrabajador> listarPorTicket(int ticketId) {
        return historialValidacionRepository.findByTicketId(ticketId);
    }

    public List<HistorialValidacionTrabajador> listarTodos() {
        return historialValidacionRepository.findAll();
    }
}