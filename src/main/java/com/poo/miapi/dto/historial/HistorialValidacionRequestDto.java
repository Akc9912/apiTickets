package com.poo.miapi.dto.historial;

public class HistorialValidacionRequestDto {
    private int idTrabajador;
    private int idTicket;
    private boolean fueResuelto;

    private String comentario; // Opcional

    public HistorialValidacionRequestDto() {
    }

    public HistorialValidacionRequestDto(int idTrabajador, int idTicket, boolean fueResuelto, String comentario) {
        this.idTrabajador = idTrabajador;
        this.idTicket = idTicket;
        this.fueResuelto = fueResuelto;
        this.comentario = comentario;
    }

    // Getters y setters
    public int getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(int idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public boolean isFueResuelto() {
        return fueResuelto;
    }

    public void setFueResuelto(boolean fueResuelto) {
        this.fueResuelto = fueResuelto;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}