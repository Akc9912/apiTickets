package com.poo.miapi.module.user.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.poo.miapi.module.ticket.enums.TicketStatus;
import com.poo.miapi.module.ticket.model.Ticket;

@Entity
@Table(name = "developer_by_ticket")
public class DeveloperByTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne(optional = false)
    @JoinColumn(name = "developer_id")
    private Developer developer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus initialStatus;

    @Enumerated(EnumType.STRING)
    private TicketStatus finalStatus;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false)
    private LocalDateTime assignmentDate;

    private LocalDateTime unassignmentDate;

    // Constructors

    public DeveloperByTicket() {
        this.assignmentDate = LocalDateTime.now();
    }

    public DeveloperByTicket(Ticket ticket, Developer developer, TicketStatus initialStatus,
            TicketStatus finalStatus, String comment, LocalDateTime assignmentDate, LocalDateTime unassignmentDate) {
        this.ticket = ticket;
        this.developer = developer;
        this.initialStatus = initialStatus;
        this.finalStatus = finalStatus;
        this.comment = comment;
        this.assignmentDate = assignmentDate != null ? assignmentDate : LocalDateTime.now();
        this.unassignmentDate = unassignmentDate;
    }

    // Getters and Setters

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Ticket getTicket() {
        return this.ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Developer getDeveloper() {
        return this.developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    public TicketStatus getInitialStatus() {
        return this.initialStatus;
    }

    public void setInitialStatus(TicketStatus initialStatus) {
        this.initialStatus = initialStatus;
    }

    public TicketStatus getFinalStatus() {
        return this.finalStatus;
    }

    public void setFinalStatus(TicketStatus finalStatus) {
        this.finalStatus = finalStatus;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getAssignmentDate() {
        return this.assignmentDate;
    }

    public void setAssignmentDate(LocalDateTime assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public LocalDateTime getUnassignmentDate() {
        return this.unassignmentDate;
    }

    public void setUnassignmentDate(LocalDateTime unassignmentDate) {
        this.unassignmentDate = unassignmentDate;
    }

    @Override
    public String toString() {
        return "DeveloperByTicket {id=" + id +
                ", ticket=" + (ticket != null ? ticket.getId() : null) +
                ", developer=" + (developer != null ? developer.getId() : null) +
                ", initialStatus=" + initialStatus +
                ", finalStatus=" + finalStatus +
                ", assignmentDate=" + assignmentDate +
                ", unassignmentDate=" + unassignmentDate +
                '}';
    }
}
