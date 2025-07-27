package com.poo.miapi.dto.tecnico;

import java.util.List;

public class TecnicoResponseDto {
    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
    private boolean cambiarPass;
    private boolean activo;
    private boolean bloqueado;
    private int fallas;
    private int marcas;
    private List<IncidenteTecnicoResponseDto> incidentes; // Usar DTO, no entidad

    // Constructor
    public TecnicoResponseDto() {
    }

    public TecnicoResponseDto(int id, String nombre, String apellido, String email, String rol,
            boolean cambiarPass, boolean activo, boolean bloqueado, int fallas, int marcas,
            List<IncidenteTecnicoResponseDto> incidentes) {
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

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isCambiarPass() {
        return cambiarPass;
    }

    public void setCambiarPass(boolean cambiarPass) {
        this.cambiarPass = cambiarPass;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

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

    public List<IncidenteTecnicoResponseDto> getIncidentes() {
        return incidentes;
    }

    public void setIncidentes(List<IncidenteTecnicoResponseDto> incidentes) {
        this.incidentes = incidentes;
    }
}