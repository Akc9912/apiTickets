package com.poo.miapi.module.user.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.poo.miapi.module.ticket.model.Ticket;
import com.poo.miapi.module.user.enums.DeveloperIncidentType;

@Entity
@Table(name = "developer_incident")
public class DeveloperIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "developer_id")
    private Developer developer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeveloperIncidentType incident;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    public DeveloperIncident() {
        this.registrationDate = LocalDateTime.now();
    }

    public DeveloperIncident(Developer developer, Ticket ticket, DeveloperIncidentType incident, String reason) {
        this.registrationDate = LocalDateTime.now();
        this.developer = developer;
        this.ticket = ticket;
        this.incident = incident;
        this.reason = reason;
    }

    // Getters y Setters

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Developer getDeveloper() {
        return this.developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public DeveloperIncidentType getIncident() {
        return this.incident;
    }

    public void setIncident(DeveloperIncidentType incident) {
        this.incident = incident;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getRegistrationDate() {
        return this.registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return "DeveloperIncident {id=" + id +
                ", developer=" + (developer != null ? developer.getId() : null) +
                ", ticket=" + (ticket != null ? ticket.getId() : null) +
                ", type=" + incident +
                ", reason='" + reason + '\'' +
                ", registrationDate=" + registrationDate +
                '}';
    }
}
