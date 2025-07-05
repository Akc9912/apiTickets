package com.poo.miapi.dto;

public class TecnicoResumenDto {
    private int id;
    private String nombre;
    private boolean bloqueado;
    private int fallas;
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