package com.poo.miapi.dto;

public class LoginResponse {
    private String nombre;
    private String tipoUsuario;
    private boolean requiereCambioPassword;

    public LoginResponse(String nombre, String tipoUsuario, boolean requiereCambioPassword) {
        this.nombre = nombre;
        this.tipoUsuario = tipoUsuario;
        this.requiereCambioPassword = requiereCambioPassword;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public boolean isRequiereCambioPassword() {
        return requiereCambioPassword;
    }
}
