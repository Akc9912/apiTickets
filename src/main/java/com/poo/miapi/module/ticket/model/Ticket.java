package com.poo.miapi.module.ticket.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.poo.miapi.module.ticket.enums.TicketStatus;
import com.poo.miapi.module.user.model.User;
import com.poo.miapi.module.user.model.Developer;

@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String tittle;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @ManyToOne
    @JoinColumn(name = "id_creador", nullable = false)
    private User creator;

    @ManyToOne
    @JoinColumn(name = "id_developer")
    private Developer developer;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Ticket() {
        this.status = TicketStatus.NOT_ATTENDED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Ticket(String tittle, String description, User creator) {
        this.tittle = tittle;
        this.description = description;
        this.creator = creator;
        this.status = TicketStatus.NOT_ATTENDED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getTtittle() {
        return tittle;
    }

    public String getDescripcion() {
        return description;
    }

    public TicketStatus getEstado() {
        return status;
    }

    public User getCreator() {
        return creator;
    }

    public LocalDateTime getCreationDate() {
        return createdAt;
    }

    public LocalDateTime getUpdateDate() {
        return updatedAt;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setCreador(User creator) {
        this.creator = creator;
    }

    public void setEstado(TicketStatus status) {
        this.status = status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updatedAt = updateDate;
    }

    @Override
    public String toString() {
        return "Ticket #" + id + ": " + tittle + " (" + status + ")";
    }
}
