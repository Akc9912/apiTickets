package com.poo.miapi.dto;

public class UsuarioResponseDto {
    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String tipo;

    public UsuarioResponseDto(int id, String nombre, String apellido, String email, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.tipo = tipo;
    }

    // Getters
    public int getId() {
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

    public String getTipo() {
        return tipo;
    }
}