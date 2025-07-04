package com.poo.miapi.service;

import com.poo.miapi.model.core.EstadoTicket;
import com.poo.miapi.repository.EstadoTicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoTicketService {

    @Autowired
    private EstadoTicketRepository estadoTicketRepository;

    public List<EstadoTicket> listarEstados() {
        return estadoTicketRepository.findAll();
    }

    public boolean existeEstado(String idEstado) {
        return estadoTicketRepository.existsById(idEstado);
    }

    public EstadoTicket obtenerPorId(String idEstado) {
        return estadoTicketRepository.findById(idEstado)
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));
    }
}