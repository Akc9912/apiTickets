package com.poo.miapi.dto.tecnico;

import java.util.List;

import com.poo.miapi.model.historial.IncidenteTecnico;

public class TecnicoResponseDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
    private boolean cambiarPass;
    private boolean activo;
    private boolean bloqueado;
    private int fallas;
    private int marcas;
    private List<IncidenteTecnico> incidentes;

    // Constructor
    public TecnicoResponseDto() {
    }

    public TecnicoResponseDto(Long id, String nombre, String apellido, String email, String rol,
            boolean cambiarPass, boolean activo, boolean bloqueado, int fallas, int marcas,
            List<IncidenteTecnico> incidentes) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rol = rol;
        this.cambiarPass = cambiarPass;
        this.activo = activo;
        this.bloqueado = bloqueado;
        this.fallas = fallas;
        this.marcas = marcas;
        this.incidentes = incidentes;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActivo() {
        return activo;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public int getFallas() {
        return fallas;
    }

    public int getMarcas() {
        return marcas;
    }

    public String getRol() {
        return rol;
    }

    public boolean isCambiarPass() {
        return cambiarPass;
    }

    public List<IncidenteTecnico> getIncidentes() {
        return incidentes;
    }
}