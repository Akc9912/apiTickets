package com.poo.miapi.module.ticket.dto;

import java.time.LocalDateTime;

import com.poo.miapi.module.ticket.enums.RefundRequestStatus;
import com.poo.miapi.module.user.dto.UserResponseDto;

public class TicketRefundRequestResponseDto {

    private int id;
    private UserResponseDto developer;
    private TicketResponseDto ticket;
    private String reason;
    private RefundRequestStatus status;
    private LocalDateTime requestDate;
    private LocalDateTime resolutionDate;
    private UserResponseDto resolvedBy;
    private String resolutionComment;

    public TicketRefundRequestResponseDto() {
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserResponseDto getDeveloper() {
        return developer;
    }

    public void setDeveloper(UserResponseDto developer) {
        this.developer = developer;
    }

    public TicketResponseDto getTicket() {
        return ticket;
    }

    public void setTicket(TicketResponseDto ticket) {
        this.ticket = ticket;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public RefundRequestStatus getStatus() {
        return status;
    }

    public void setStatus(RefundRequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDateTime getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(LocalDateTime resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public UserResponseDto getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(UserResponseDto resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public String getResolutionComment() {
        return resolutionComment;
    }

    public void setResolutionComment(String resolutionComment) {
        this.resolutionComment = resolutionComment;
    }
}
