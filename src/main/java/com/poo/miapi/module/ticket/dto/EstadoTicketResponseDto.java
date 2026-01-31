package com.poo.miapi.module.ticket.dto;

public class EstadoTicketResponseDto {
    private String id; // nombre del enum, ej: "NO_ATENDIDO"
    private String label; // descripción legible, ej: "No atendido"

    public EstadoTicketResponseDto() {
    }

    public EstadoTicketResponseDto(String id, String label) {
        this.id = id;
        this.label = label;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
