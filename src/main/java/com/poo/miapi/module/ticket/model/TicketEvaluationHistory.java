package com.poo.miapi.module.ticket.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.poo.miapi.module.user.model.User;

@Entity
@Table(name = "ticket_evaluation_history")
public class TicketEvaluationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User evaluatorUser;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Column(nullable = false)
    private boolean wasApproved;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(nullable = false)
    private LocalDateTime evaluationDate;

    public TicketEvaluationHistory() {
        this.evaluationDate = LocalDateTime.now();
    }

    public TicketEvaluationHistory(User evaluatorUser, Ticket ticket, boolean wasApproved, String comments) {
        this.evaluatorUser = evaluatorUser;
        this.ticket = ticket;
        this.wasApproved = wasApproved;
        this.comments = comments;
        this.evaluationDate = LocalDateTime.now();
    }

    // Getters y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getEvaluatorUser() {
        return evaluatorUser;
    }

    public void setEvaluatorUser(User evaluatorUser) {
        this.evaluatorUser = evaluatorUser;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public boolean isWasApproved() {
        return wasApproved;
    }

    public void setWasApproved(boolean wasApproved) {
        this.wasApproved = wasApproved;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDateTime evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    @Override
    public String toString() {
        return "TicketEvaluationHistory {id=" + id +
                ", evaluatorUser=" + (evaluatorUser != null ? evaluatorUser.getId() : null) +
                ", ticket=" + (ticket != null ? ticket.getId() : null) +
                ", wasApproved=" + wasApproved +
                ", comments='" + comments + '\'' +
                ", evaluationDate=" + evaluationDate +
                '}';
    }
}
