package com.poo.miapi.module.ticket.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.poo.miapi.module.ticket.enums.RefundRequestStatus;
import com.poo.miapi.module.user.model.Admin;
import com.poo.miapi.module.user.model.Developer;

@Entity
@Table(name = "ticket_refund_requests")
public class TicketRefundRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "developer_id")
    private Developer developer;
    @ManyToOne(optional = false)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;
    @Column(nullable = false, length = 500)
    private String reason;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RefundRequestStatus status;
    @Column(nullable = false)
    private LocalDateTime requestDate;
    private LocalDateTime resolutionDate;
    @ManyToOne
    @JoinColumn(name = "resolved_by_id")
    private Admin resolvedBy;
    @Column(length = 500)
    private String resolutionComment;

    public TicketRefundRequest() {
        this.status = RefundRequestStatus.PENDING;
        this.requestDate = LocalDateTime.now();
    }

    public TicketRefundRequest(Developer developer, Ticket ticket, String reason) {
        this();
        this.developer = developer;
        this.ticket = ticket;
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

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public RefundRequestStatus getStatus() {
        return this.status;
    }

    public void setStatus(RefundRequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestDate() {
        return this.requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDateTime getResolutionDate() {
        return this.resolutionDate;
    }

    public void setResolutionDate(LocalDateTime resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public Admin getResolvedBy() {
        return this.resolvedBy;
    }

    public void setResolvedBy(Admin resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public String getResolutionComment() {
        return this.resolutionComment;
    }

    public void setResolutionComment(String resolutionComment) {
        this.resolutionComment = resolutionComment;
    }

}
