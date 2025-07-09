package com.poo.miapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TecnicoResumenDto {
    @NotNull(message = "El id del técnico no puede ser nulo")
    private int id;

    @NotBlank(message = "El nombre del técnico es obligatorio")
    private String nombre;

    @NotNull(message = "El estado de bloqueo del técnico no puede ser nulo")
    private boolean bloqueado;

    @NotNull(message = "El número de fallas no puede ser nulo")
    private int fallas;

    @NotNull(message = "El número de marcas no puede ser nulo")
    private int marcas;

    public TecnicoResumenDto(int id, String nombre, boolean bloqueado, int fallas, int marcas) {
        this.id = id;
        this.nombre = nombre;
        this.bloqueado = bloqueado;
        this.fallas = fallas;
        this.marcas = marcas;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
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
}