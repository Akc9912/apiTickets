package com.poo.miapi.dto;

public class UsuarioBasicoDto {
    private int id;
    private String nombre;

    public UsuarioBasicoDto(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}