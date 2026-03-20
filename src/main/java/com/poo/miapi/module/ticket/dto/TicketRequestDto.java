package com.poo.miapi.module.ticket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to create a new ticket")
public class TicketRequestDto {

    @Schema(description = "Ticket title", example = "Unable to access user dashboard", required = true)
    @NotBlank(message = "El título es obligatorio")
    private String tittle;

    @Schema(description = "Detailed description of the issue", example = "When I try to log in to the dashboard, I get a 500 error", required = true)
    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @Schema(description = "ID of the user creating the ticket", example = "1", required = true)
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
