package com.poo.miapi.model.core;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.poo.miapi.model.historial.IncidenteTecnico;
import com.poo.miapi.model.historial.TecnicoPorTicket;

@Entity
@DiscriminatorValue("TECNICO")
public class Tecnico extends Usuario {

    /* ---------- Campos propios ---------- */
    private int fallas = 0;
    private int marcas = 0;

    /* Incidentes disciplinarios registrados al técnico */
    @OneToMany(mappedBy = "tecnico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidenteTecnico> incidentes = new ArrayList<>();

    /* Historial de tickets atendidos por el técnico */
    @OneToMany(mappedBy = "tecnico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TecnicoPorTicket> historialTecnicos = new ArrayList<>();

    /* ---------- Constructores ---------- */
    public Tecnico() {
        super();
        this.setRol(Rol.TECNICO);
    }

    public Tecnico(String nombre, String apellido, String email) {
        super(nombre, apellido, email);
        this.setRol(Rol.TECNICO);
    }

    /* ---------- Getters / Setters ---------- */
    public int getFallas() {
        return fallas;
    }

    public void setFallas(int fallas) {
        this.fallas = fallas;
    }

    public int getMarcas() {
        return marcas;
    }

    public void setMarcas(int marcas) {
        this.marcas = marcas;
    }

    public List<IncidenteTecnico> getIncidentes() {
        return Collections.unmodifiableList(incidentes);
    }

    public void setIncidentes(List<IncidenteTecnico> inc) {
        this.incidentes = inc;
    }

    public List<TecnicoPorTicket> getHistorialTecnicos() {
        return Collections.unmodifiableList(historialTecnicos);
    }

    public void setHistorialTecnicos(List<TecnicoPorTicket> hist) {
        this.historialTecnicos = hist;
    }

    /* ---------- Utilidades de dominio ---------- */
    public void addIncidente(IncidenteTecnico incidente) {
        if (incidente != null) {
            incidentes.add(incidente);
            incidente.setTecnico(this);
        }
    }

    public void addEntradaHistorial(TecnicoPorTicket entrada) {
        if (entrada != null) {
            historialTecnicos.add(entrada);
            entrada.setTecnico(this);
        }
    }

    /**
     * Devuelve los tickets actualmente asignados al técnico (sin fecha de
     * desasignación).
     */
    public List<Ticket> getTicketsActuales() {
        return historialTecnicos.stream()
                .filter(h -> h.getFechaDesasignacion() == null)
                .map(TecnicoPorTicket::getTicket)
                .collect(Collectors.toList());
    }

    @Override
    public String getTipoUsuario() {
        return "TECNICO";
    }
}
