package com.poo.miapi.model.core;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.poo.miapi.model.historial.IncidenteTecnico;

@Entity
@DiscriminatorValue("TECNICO")
public class Tecnico extends Usuario {

    private int fallas = 0;
    private int marcas = 0;

    @OneToMany(mappedBy = "tecnico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidenteTecnico> incidentes = new ArrayList<>();

    private List<Ticket> misTickets = new ArrayList<>();

    public Tecnico() {
        super();
        this.misTickets = new ArrayList<>();
        this.setRol("TECNICO");
    }

    public Tecnico(String nombre, String apellido, String email) {
        super(nombre, apellido, email);
        this.misTickets = new ArrayList<>();
        this.setRol("TECNICO");
    }

    public int getFallas() {
        return fallas;
    }

    public int getMarcas() {
        return marcas;
    }

    public List<IncidenteTecnico> getIncidentes() {
        return incidentes;
    }

    public void setFallas(int valor) {
        this.fallas = valor;
    }

    public void setMarcas(int valor) {
        this.marcas = valor;
    }

    public void setIncidentes(List<IncidenteTecnico> incidentes) {
        this.incidentes = incidentes;
    }

    public void addIncidente(IncidenteTecnico incidente) {
        this.incidentes.add(incidente);
        incidente.setTecnico(this);
    }

    public void removeIncidente(IncidenteTecnico incidente) {
        this.incidentes.remove(incidente);
        incidente.setTecnico(null);
    }

    public List<Ticket> getMisTickets() {
        return misTickets;
    }

    public void setMisTickets(List<Ticket> misTickets) {
        this.misTickets = misTickets;
    }

    public void addTicket(Ticket ticket) {
        this.misTickets.add(ticket);
    }

    public void removeTicket(Ticket ticket) {
        this.misTickets.remove(ticket);
    }

    @Override
    public String getTipoUsuario() {
        return "TECNICO";
    }

}
