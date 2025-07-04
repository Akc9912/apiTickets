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

    public void setFallas(int valor) {
        this.fallas = valor;
    }

    public void setMarcas(int valor) {
        this.marcas = valor;
    }

    public void sumarFalla() {
        this.fallas++;
    }

    public void sumarMarca() {
        this.marcas++;
    }

    public void descontarFalla() {
        this.fallas--;
    }

    public void descontarMarca() {
        this.marcas--;
    }

    public void reiniciarFallasYMarcas() {
        setFallas(0);
        setMarcas(0);
    }

    @Override
    public String getTipoUsuario() {
        return "TECNICO";
    }

}
