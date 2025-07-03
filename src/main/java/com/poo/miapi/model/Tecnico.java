package com.poo.miapi.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("TECNICO")
public class Tecnico extends Usuario {

    private int fallas = 0;
    private int marcas = 0;

    @OneToMany(mappedBy = "tecnico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidenteTecnico> incidentes = new ArrayList<>();

    public Tecnico() {
        super();
    }

    public Tecnico(String nombre, String apellido, String email) {
        super(nombre, apellido, email);
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

    @Override
    public String getTipoUsuario() {
        return "TECNICO";
    }

}
