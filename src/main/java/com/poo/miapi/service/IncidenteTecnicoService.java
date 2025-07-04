package com.poo.miapi.service;

import com.poo.miapi.model.historial.IncidenteTecnico;
import com.poo.miapi.repository.IncidenteTecnicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidenteTecnicoService {

    @Autowired
    private IncidenteTecnicoRepository incidenteTecnicoRepository;

    public List<IncidenteTecnico> listarTodos() {
        return incidenteTecnicoRepository.findAll();
    }

    public List<IncidenteTecnico> listarPorTecnico(int idTecnico) {
        return incidenteTecnicoRepository.findByTecnicoId(idTecnico);
    }

    public long contarPorTecnico(int idTecnico) {
        return incidenteTecnicoRepository.findByTecnicoId(idTecnico).size();
    }

    public void registrarIncidente(IncidenteTecnico incidente) {
        incidenteTecnicoRepository.save(incidente);
    }
}