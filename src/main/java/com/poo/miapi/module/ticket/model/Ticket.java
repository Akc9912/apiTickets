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
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne
    @JoinColumn(name = "developer_id", nullable = true)
    private Developer developer;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Ticket() {
        this.status = TicketStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Ticket(String title, String description, User creator) {
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.status = TicketStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TicketStatus getStatus() {
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

    public void setCreator(User creator) {
        this.creator = creator;
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
        return "Ticket #" + id + ": " + title + " (" + status + ")";
    }

    public Ticket orElseThrow(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
    }
}
