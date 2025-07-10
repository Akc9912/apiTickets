package com.poo.miapi.dto.admin;

public class AdminResponseDto {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
    private boolean activo;
    private boolean bloqueado;

    // Constructor
    public AdminResponseDto() {
    }

    public AdminResponseDto(Long id, String nombre, String apellido, String email, String rol,
            boolean activo, boolean bloqueado) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rol = rol;
        this.activo = activo;
        this.bloqueado = bloqueado;
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

    public String getRol() {
        return rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }
}
