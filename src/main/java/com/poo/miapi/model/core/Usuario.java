
package com.poo.miapi.model.core;
import com.poo.miapi.model.enums.Rol;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_usuario")
@Entity

public abstract class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    private String apellido;

    @Column(unique = true)
    private String email;

    private String password;
    @Enumerated(EnumType.STRING)
    private Rol rol;
    private boolean cambiarPass;
    private boolean activo;
    private boolean bloqueado;

    // Constructor vacío requerido por JPA
    public Usuario() {
    }

    // Constructor completo para subclases (sin rol, activo ni password como
    // parámetro)
    public Usuario(String nombre, String apellido, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = null;
        this.rol = null; // Se asigna en la subclase
        this.cambiarPass = true;
        this.activo = true;
        this.bloqueado = false;
    }

    // Getters
    public int getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getApellido() {
        return this.apellido;
    }

    public String getEmail() {
        return this.email;
    }

    // Implementación de UserDetails
    @Override
        public java.util.Collection<? extends GrantedAuthority> getAuthorities() {
            // Usa siempre el formato ROLE_SUPER_ADMIN para el rol de superadmin
            String roleName = rol != null ? rol.name() : "USER";
            return java.util.List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.bloqueado;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.activo;
    }

    public String getPassword() {
        return this.password;
    }

    public Rol getRol() {
        return this.rol;
    }

    public boolean isCambiarPass() {
        return this.cambiarPass;
    }

    public boolean isActivo() {
        return this.activo;
    }

    public boolean isBloqueado() {
        return this.bloqueado;
    }

    // Setters
    public void setNombre(String unNombre) {
        this.nombre = unNombre;
    }

    public void setApellido(String unApellido) {
        this.apellido = unApellido;
    }

    public void setEmail(String unEmail) {
        this.email = unEmail;
    }

    public void setPassword(String hashedPassword) {
        this.password = hashedPassword;
    }

    public void setRol(Rol unRol) {
        this.rol = unRol;
    }

    public void setCambiarPass(boolean valor) {
        this.cambiarPass = valor;
    }

    public void setActivo(boolean valor) {
        this.activo = valor;
    }

    public void setBloqueado(boolean valor) {
        this.bloqueado = valor;
    }

    // Métodos de validación de estado
    
    /**
     * Verifica si el usuario puede iniciar sesión
     * Solo usuarios activos pueden iniciar sesión
     */
    public boolean puedeIniciarSesion() {
        return this.activo;
    }
    
    /**
     * Verifica si el usuario puede realizar acciones en el sistema
     * Usuarios activos y no bloqueados pueden realizar acciones
     */
    public boolean puedeRealizarAcciones() {
        return this.activo && !this.bloqueado;
    }

    // Método abstracto para tipo de usuario
    public abstract String getTipoUsuario();

    // Sobreescritura de métodos
    @Override
    public String toString() {
        return "[" + getTipoUsuario() + "] " + nombre + " (ID: " + id + ")";
    }
}