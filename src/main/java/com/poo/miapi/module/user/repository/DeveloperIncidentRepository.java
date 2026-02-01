package com.poo.miapi.module.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poo.miapi.module.user.model.DeveloperIncident;
import com.poo.miapi.module.user.enums.DeveloperIncidentType;

@Repository
public interface DeveloperIncidentRepository extends JpaRepository<DeveloperIncident, Integer> {

    /**
     * Encuentra todos los incidentes de un desarrollador específico
     */
    @Query("SELECT di FROM DeveloperIncident di WHERE di.developer.id = :developerId ORDER BY di.registrationDate DESC")
    List<DeveloperIncident> findByDeveloperId(@Param("developerId") int developerId);

    /**
     * Encuentra todos los incidentes relacionados con un ticket
     */
    @Query("SELECT di FROM DeveloperIncident di WHERE di.ticket.id = :ticketId ORDER BY di.registrationDate DESC")
    List<DeveloperIncident> findByTicketId(@Param("ticketId") int ticketId);

    /**
     * Encuentra incidentes por tipo
     */
    @Query("SELECT di FROM DeveloperIncident di WHERE di.incident = :type ORDER BY di.registrationDate DESC")
    List<DeveloperIncident> findByIncidentType(@Param("type") DeveloperIncidentType type);

    /**
     * Cuenta los incidentes de un desarrollador por tipo
     */
    @Query("SELECT COUNT(di) FROM DeveloperIncident di WHERE di.developer.id = :developerId AND di.incident = :type")
    long countByDeveloperIdAndIncidentType(@Param("developerId") int developerId,
            @Param("type") DeveloperIncidentType type);
}
