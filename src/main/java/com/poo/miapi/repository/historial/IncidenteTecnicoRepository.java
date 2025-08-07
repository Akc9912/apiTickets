package com.poo.miapi.repository.historial;

import com.poo.miapi.model.historial.IncidenteTecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidenteTecnicoRepository extends JpaRepository<IncidenteTecnico, Integer> {

    List<IncidenteTecnico> findByTecnicoId(int tecnicoId);

    List<IncidenteTecnico> findByTicketId(int ticketId);

    int countByTecnicoId(int tecnicoId);

    List<IncidenteTecnico> findByTipo(IncidenteTecnico.TipoIncidente tipo);
}