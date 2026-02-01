package com.poo.miapi.module.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TicketRequestDto {

    @NotBlank(message = "El título es obligatorio")
    private String tittle;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotNull(message = "El ID del creador es obligatorio")
    private int creatorId;

    public TicketRequestDto() {
    }

    public TicketRequestDto(String tittle, String description, int creatorId) {
        this.tittle = tittle;
        this.description = description;
        this.creatorId = creatorId;
    }

    // Getters y setters
    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }
}
