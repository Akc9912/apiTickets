package com.poo.miapi.module.user.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.poo.miapi.module.ticket.model.Ticket;
import com.poo.miapi.module.user.enums.UserRole;

@Entity
@Table(name = "developer")
@DiscriminatorValue("DEVELOPER")
public class Developer extends User {

    private int warnings = 0;
    private int failures = 0;

    // Incidentes registrados al técnico
    @OneToMany(mappedBy = "developer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeveloperIncident> incidentsHistory = new ArrayList<>();

    // Historial de tickets atendidos por el técnico
    @OneToMany(mappedBy = "developer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeveloperByTicket> ticketHistory = new ArrayList<>();

    public Developer() {
        super();
        this.setRole(UserRole.DEVELOPER);
        this.incidentsHistory = new ArrayList<>();
        this.ticketHistory = new ArrayList<>();
    }

    public Developer(String name, String lastName, String email) {
        super(name, lastName, email);
        this.setRole(UserRole.DEVELOPER);
        this.incidentsHistory = new ArrayList<>();
        this.ticketHistory = new ArrayList<>();
    }

    // Getters / Setters
    public int getFailures() {
        return failures;
    }

    public void setFailures(int failures) {
        this.failures = failures;
    }

    public int getWarnings() {
        return warnings;
    }

    public void setWarnings(int warnings) {
        this.warnings = warnings;
    }

    public List<DeveloperIncident> getIncidentes() {
        return Collections.unmodifiableList(incidentsHistory);
    }

    public void setIncident(List<DeveloperIncident> incident) {
        this.incidentsHistory = incident;
    }

    public List<DeveloperByTicket> getHistorialTecnicos() {
        return Collections.unmodifiableList(ticketHistory);
    }

    public void setHistorialTecnicos(List<DeveloperByTicket> historial) {
        this.ticketHistory = historial;
    }

    public void addIncident(DeveloperIncident incident) {
        if (incident != null) {
            incidentsHistory.add(incident);
            incident.setDeveloper(this);
        }
    }

    public void addTicketToHistorial(DeveloperByTicket register) {
        if (register != null) {
            ticketHistory.add(register);
            register.setDeveloper(this);
        }
    }

    public List<Ticket> getTicketsActuales() {
        return ticketHistory.stream()
                .filter(h -> h.getUnassignmentDate() == null)
                .map(DeveloperByTicket::getTicket)
                .collect(Collectors.toList());
    }

    @Override
    public String getUserType() {
        return "DEVELOPER";
    }
}
