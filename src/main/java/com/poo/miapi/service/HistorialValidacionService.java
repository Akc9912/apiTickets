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

    public List<HistorialValidacionTrabajador> listarTodos() {
        return historialValidacionRepository.findAll();
    }

    public void registrarValidacion(HistorialValidacionTrabajador validacion) {
        historialValidacionRepository.save(validacion);
    }
}
