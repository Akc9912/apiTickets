package com.poo.miapi.model.core;
import com.poo.miapi.model.enums.EstadoTicket;

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

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTicket estado;

    @ManyToOne
    @JoinColumn(name = "id_creador", nullable = false)
    private Usuario creador;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TecnicoPorTicket> historialTecnicos = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaUltimaActualizacion;

    // Constructor requerido por JPA
    public Ticket() {
        this.estado = EstadoTicket.NO_ATENDIDO;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaUltimaActualizacion = LocalDateTime.now();
    }

    public Ticket(String titulo, String descripcion, Usuario creador) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.creador = creador;
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

    public Usuario getCreador() {
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
    public void setCreador(Usuario creador) {
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

        // Devuelve el técnico actualmente asignado (sin fecha de desasignación)
        public Tecnico getTecnicoActual() {
            for (int i = historialTecnicos.size() - 1; i >= 0; i--) {
                TecnicoPorTicket entrada = historialTecnicos.get(i);
                if (entrada.getFechaDesasignacion() == null) {
                    return entrada.getTecnico();
                }
            }
            return null;
        }

        // Devuelve el último técnico que atendió el ticket, aunque esté desasignado
        public Tecnico getUltimoTecnicoAtendio() {
            for (int i = historialTecnicos.size() - 1; i >= 0; i--) {
                TecnicoPorTicket entrada = historialTecnicos.get(i);
                if (entrada.getTecnico() != null) {
                    return entrada.getTecnico();
                }
            }
            return null;
        }

    @Override
    public String toString() {
        return "Ticket #" + id + ": " + titulo + " (" + estado + ")";
    }
}
