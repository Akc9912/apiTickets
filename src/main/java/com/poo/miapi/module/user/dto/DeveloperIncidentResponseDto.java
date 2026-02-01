package com.poo.miapi.module.user.dto;

import java.time.LocalDateTime;

import com.poo.miapi.module.user.enums.DeveloperIncidentType;
import com.poo.miapi.module.user.model.DeveloperIncident;

public class DeveloperIncidentResponseDto {
    private int id;
    private int developerId;
    private int ticketId;
    private String incidentType;
    private String reason;
    private String registeredAt;

    public DeveloperIncidentResponseDto() {
    }

    public DeveloperIncidentResponseDto(int id, int developerId, int ticketId, DeveloperIncidentType incidentType,
            String reason, LocalDateTime registeredAt) {
        this.id = id;
        this.developerId = developerId;
        this.ticketId = ticketId;
        this.incidentType = incidentType.toString();
        this.reason = reason;
        this.registeredAt = registeredAt.toString();
    }

    public DeveloperIncidentResponseDto(DeveloperIncident incident) {
        this.id = incident.getId();
        this.developerId = incident.getDeveloper().getId();
        this.ticketId = incident.getTicket().getId();
        this.incidentType = incident.getIncident().toString();
        this.reason = incident.getReason();
        this.registeredAt = incident.getRegistrationDate().toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(int developerId) {
        this.developerId = developerId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }
}
