package com.poo.miapi.module.audit.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poo.miapi.module.audit.model.IncidenteTecnico;
import com.poo.miapi.shared.events.enums.TipoIncidente;

import java.util.List;

@Repository
public interface IncidenteTecnicoRepository extends JpaRepository<IncidenteTecnico, Integer> {

    List<IncidenteTecnico> findByTecnicoId(int tecnicoId);

    List<IncidenteTecnico> findByTicketId(int ticketId);

    int countByTecnicoId(int tecnicoId);

    List<IncidenteTecnico> findByTipo(TipoIncidente tipo);
}
