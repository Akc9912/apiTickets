package com.poo.miapi.dto.ticket;

public class TicketRequestDto {
    private String titulo;
    private String descripcion;
    private int idTrabajador;

    public TicketRequestDto() {
    }

    public TicketRequestDto(String titulo, String descripcion, int idTrabajador) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.idTrabajador = idTrabajador;
    }

    // Getters y setters
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(int idTrabajador) {
        this.idTrabajador = idTrabajador;
    }
}
