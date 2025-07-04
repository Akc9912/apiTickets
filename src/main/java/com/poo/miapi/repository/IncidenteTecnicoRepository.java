package com.poo.miapi.repository;

import com.poo.miapi.model.historial.IncidenteTecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidenteTecnicoRepository extends JpaRepository<IncidenteTecnico, Long> {
    List<IncidenteTecnico> findByTecnicoId(int tecnicoId);
}