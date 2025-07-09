package com.poo.miapi.model.core;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.poo.miapi.model.historial.TecnicoPorTicket;

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

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TecnicoPorTicket> historialTecnicos;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaCreacion;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaUltimaActualizacion;

    // Constructor requerido por JPA
    public Ticket() {
    }

    public Ticket(String titulo, String descripcion, Trabajador creador) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.creador = creador;
        this.historialTecnicos = new ArrayList<>();
        this.estado = EstadoTicket.NO_ATENDIDO;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaUltimaActualizacion = LocalDateTime.now();
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaUltimaActualizacion() {
        return fechaUltimaActualizacion;
    }

    public List<TecnicoPorTicket> getHistorialTecnicos() {
        return historialTecnicos;
    }

    // Setters
    public void setCreador(Trabajador creador) {
        this.creador = creador;
    }

    public void setEstado(EstadoTicket estado) {
        this.estado = estado;
    }

    public void setFechaUltimaActualizacion(LocalDateTime fechaUltimaActualizacion) {
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }

    public void agregarEntradaHistorial(TecnicoPorTicket entrada) {
        historialTecnicos.add(entrada);
        entrada.setTicket(this);
    }

    // Utilidades
    public Tecnico getTecnicoActual() {
        if (historialTecnicos.isEmpty())
            return null;
        return historialTecnicos.get(historialTecnicos.size() - 1).getTecnico();
    }

    @Override
    public String toString() {
        return "Ticket #" + id + ": " + titulo + " (" + estado + ")";
    }

}
