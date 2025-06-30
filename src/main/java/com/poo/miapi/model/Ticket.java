package com.poo.miapi.model;

public class Ticket {
    private static int contadorId = 1;

    private int id;
    private String titulo;
    private String descripcion;
    private EstadoTicket estado;
    private Trabajador creador;
    private Tecnico tecnicoActual;
    private Tecnico tecnicoAnterior;

    public Ticket(String titulo, String descripcion, Trabajador creador) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El titulo no puede estar vacío.");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripcion no puede estar vacía.");
        }

        this.id = Ticket.contadorId++;
        this.descripcion = descripcion;
        this.estado = estado; // no
        this.creador = creador;
    }

}
