package com.poo.miapi.model;

import jakarta.persistence.*;

@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String titulo;
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoTicket estado;

    @ManyToOne
    @JoinColumn(name = "id_creador")
    private Trabajador creador;

    @ManyToOne
    @JoinColumn(name = "id_tecnico_actual")
    private Tecnico tecnicoActual;

    @ManyToOne
    @JoinColumn(name = "id_tecnico_anterior")
    private Tecnico tecnicoAnterior;

    public Ticket() {
        // Requerido por JPA
    }

    public Ticket(String titulo, String descripcion, Trabajador creador) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío.");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía.");
        }

        this.titulo = titulo;
        this.descripcion = descripcion;
        this.creador = creador;
        this.estado = EstadoTicket.NO_ATENDIDO;
        this.tecnicoActual = null;
        this.tecnicoAnterior = null;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public EstadoTicket getEstado() {
        return estado;
    }

    public Trabajador getCreador() {
        return creador;
    }

    public Tecnico getTecnicoActual() {
        return tecnicoActual;
    }

    public Tecnico getTecnicoAnterior() {
        return tecnicoAnterior;
    }

    // Setters
    public void setCreador(Trabajador creador) {
        if (creador == null) {
            throw new IllegalArgumentException("El creador no puede ser null.");
        }
        this.creador = creador;
    }

    // Métodos funcionales
    public boolean puedeSerTomado() {
        return this.estado == EstadoTicket.NO_ATENDIDO;
    }

    public void asignarTecnico(Tecnico tecnico) {
        if (!puedeSerTomado()) {
            throw new IllegalStateException("El ticket ya está siendo atendido.");
        }
        this.tecnicoActual = tecnico;
        this.estado = EstadoTicket.ATENDIDO;
    }

    public void desasignarTecnico() {
        this.tecnicoAnterior = this.tecnicoActual;
        this.tecnicoActual = null;
        this.estado = EstadoTicket.NO_ATENDIDO;
    }

    public void marcarResuelto() {
        if (this.estado != EstadoTicket.ATENDIDO) {
            throw new IllegalStateException("Solo se pueden resolver tickets en estado 'ATENDIDO'.");
        }
        this.estado = EstadoTicket.RESUELTO;
    }

    public void marcarFinalizado() {
        if (this.estado != EstadoTicket.RESUELTO) {
            throw new IllegalStateException("Solo se pueden finalizar tickets en estado 'RESUELTO'.");
        }
        this.estado = EstadoTicket.FINALIZADO;
    }

    public void marcarReabierto() {
        if (this.estado != EstadoTicket.RESUELTO) {
            throw new IllegalStateException("Solo se pueden reabrir tickets en estado 'RESUELTO'.");
        }
        this.tecnicoAnterior = this.tecnicoActual;
        this.tecnicoActual = null;
        this.estado = EstadoTicket.REABIERTO;
    }

    public void reabrir(Tecnico tecnicoQueLoPide) {
        if (!tecnicoQueLoPide.equals(this.tecnicoActual)) {
            throw new IllegalArgumentException("Solo el técnico actual puede pedir la reapertura.");
        }
        this.tecnicoAnterior = this.tecnicoActual;
        this.tecnicoActual = null;
        this.estado = EstadoTicket.REABIERTO;
    }

    @Override
    public String toString() {
        return "Ticket #" + id + ": " + titulo + " (" + estado + ")";
    }
}
