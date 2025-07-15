package com.poo.miapi.dto.historial;

import java.time.LocalDateTime;
import com.poo.miapi.model.core.EstadoTicket;

public class TecnicoPorTicketResponseDto {
    private Long id;
    private Long idTicket;
    private Long idTecnico;
    private EstadoTicket estadoInicial;
    private EstadoTicket estadoFinal;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaDesasignacion;
    private String comentario;

    public TecnicoPorTicketResponseDto() {
    }

    public TecnicoPorTicketResponseDto(Long id, Long idTicket, Long idTecnico, EstadoTicket estadoInicial,
            EstadoTicket estadoFinal, LocalDateTime fechaAsignacion,
            LocalDateTime fechaDesasignacion, String comentario) {
        this.id = id;
        this.idTicket = idTicket;
        this.idTecnico = idTecnico;
        this.estadoInicial = estadoInicial;
        this.estadoFinal = estadoFinal;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaDesasignacion = fechaDesasignacion;
        this.comentario = comentario;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(Long idTicket) {
        this.idTicket = idTicket;
    }

    public Long getIdTecnico() {
        return idTecnico;
    }

    public void setIdTecnico(Long idTecnico) {
        this.idTecnico = idTecnico;
    }

    public EstadoTicket getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(EstadoTicket estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public EstadoTicket getEstadoFinal() {
        return estadoFinal;
    }

    public void setEstadoFinal(EstadoTicket estadoFinal) {
        this.estadoFinal = estadoFinal;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public LocalDateTime getFechaDesasignacion() {
        return fechaDesasignacion;
    }

    public void setFechaDesasignacion(LocalDateTime fechaDesasignacion) {
        this.fechaDesasignacion = fechaDesasignacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}