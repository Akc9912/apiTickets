package com.poo.miapi.dto.tecnico;

import java.time.LocalDateTime;
import com.poo.miapi.model.historial.IncidenteTecnico.TipoIncidente;

public class IncidenteTecnicoResponseDto {
    private Long id;
    private Long idTecnico;
    private Long idTicket;
    private TipoIncidente tipo;
    private String motivo;
    private LocalDateTime fechaRegistro;

    public IncidenteTecnicoResponseDto() {
    }

    public IncidenteTecnicoResponseDto(Long id, Long idTecnico, Long idTicket, TipoIncidente tipo, String motivo,
            LocalDateTime fechaRegistro) {
        this.id = id;
        this.idTecnico = idTecnico;
        this.idTicket = idTicket;
        this.tipo = tipo;
        this.motivo = motivo;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdTecnico() {
        return idTecnico;
    }

    public void setIdTecnico(Long idTecnico) {
        this.idTecnico = idTecnico;
    }

    public Long getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(Long idTicket) {
        this.idTicket = idTicket;
    }

    public TipoIncidente getTipo() {
        return tipo;
    }

    public void setTipo(TipoIncidente tipo) {
        this.tipo = tipo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
