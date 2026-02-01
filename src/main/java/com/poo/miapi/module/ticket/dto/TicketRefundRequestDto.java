package com.poo.miapi.module.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TicketRefundRequestDto {

    @NotNull(message = "Developer ID is required")
    private Integer developerId;

    @NotNull(message = "Ticket ID is required")
    private Integer ticketId;

    @NotBlank(message = "Reason is required")
    @Size(min = 10, max = 500, message = "Reason must be between 10 and 500 characters")
    private String reason;

    public TicketRefundRequestDto() {
    }

    public TicketRefundRequestDto(Integer developerId, Integer ticketId, String reason) {
        this.developerId = developerId;
        this.ticketId = ticketId;
        this.reason = reason;
    }

    // Getters y Setters
    public Integer getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(Integer developerId) {
        this.developerId = developerId;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
