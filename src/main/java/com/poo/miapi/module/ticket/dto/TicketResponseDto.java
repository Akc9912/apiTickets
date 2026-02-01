package com.poo.miapi.module.ticket.dto;

import java.time.LocalDateTime;

import com.poo.miapi.module.ticket.enums.TicketStatus;
import com.poo.miapi.module.user.dto.DeveloperResponseDto;
import com.poo.miapi.module.user.dto.UserResponseDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TicketResponseDto {
    @NotNull(message = "El id del ticket no puede ser nulo")
    private int id;

    @NotBlank(message = "El título del ticket es obligatorio")
    private String tittle;

    @NotBlank(message = "La descripción del ticket es obligatoria")
    private String description;

    @NotNull(message = "El estado del ticket es obligatorio")
    private TicketStatus statis;

    private UserResponseDto creator;

    private DeveloperResponseDto Developer; // null si nadie lo tomo o ya esta desasignado

    @NotNull(message = "La fecha de creación no puede ser nula")
    private LocalDateTime creationDate;

    @NotNull(message = "La fecha de última actualización no puede ser nula")
    private LocalDateTime UpdateDate;

    // Constructor
    public TicketResponseDto() {
    }

    public TicketResponseDto(int id, String tittle, String description, TicketStatus statis,
            UserResponseDto creator, DeveloperResponseDto developer, LocalDateTime creationDate,
            LocalDateTime updateDate) {
        this.id = id;
        this.tittle = tittle;
        this.description = description;
        this.statis = statis;
        this.creator = creator;
        this.Developer = developer;
        this.creationDate = creationDate;
        this.UpdateDate = updateDate;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public TicketStatus getStatis() {
        return statis;
    }

    public void setStatis(TicketStatus statis) {
        this.statis = statis;
    }

    public UserResponseDto getCreator() {
        return creator;
    }

    public void setCreator(UserResponseDto creator) {
        this.creator = creator;
    }

    public DeveloperResponseDto getDeveloper() {
        return Developer;
    }

    public void setDeveloper(DeveloperResponseDto developer) {
        this.Developer = developer;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getUpdateDate() {
        return UpdateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.UpdateDate = updateDate;
    }
}