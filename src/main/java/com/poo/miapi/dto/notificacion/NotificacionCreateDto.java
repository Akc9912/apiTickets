package com.poo.miapi.dto.notificacion;

import com.poo.miapi.model.enums.TipoNotificacion;
import com.poo.miapi.model.enums.CategoriaNotificacion;
import com.poo.miapi.model.enums.PrioridadNotificacion;
import com.poo.miapi.model.enums.SeveridadNotificacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Map;

public class NotificacionCreateDto {

    @NotNull(message = "El ID del usuario es requerido")
    private int usuarioId;

    @NotBlank(message = "El título es requerido")
    @Size(min = 1, max = 200, message = "El título debe tener entre 1 y 200 caracteres")
    private String titulo;

    @NotBlank(message = "El mensaje es requerido")
    @Size(min = 1, max = 1000, message = "El mensaje debe tener entre 1 y 1000 caracteres")
    private String mensaje;

    @NotNull(message = "El tipo de notificación es requerido")
    private TipoNotificacion tipo;

    @NotNull(message = "La categoría es requerida")
    private CategoriaNotificacion categoria;

    private PrioridadNotificacion prioridad = PrioridadNotificacion.MEDIA;

    private SeveridadNotificacion severidad = SeveridadNotificacion.INFO;

    private String entidadTipo;

    private Integer entidadId;

    private Map<String, Object> datosAdicionales;

    private LocalDateTime fechaExpiracion;

    // Constructores
    public NotificacionCreateDto() {
    }

    public NotificacionCreateDto(int usuarioId, String titulo, String mensaje,
            TipoNotificacion tipo, CategoriaNotificacion categoria) {
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.categoria = categoria;
    }

    // Getters y Setters
    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoNotificacion tipo) {
        this.tipo = tipo;
    }

    public CategoriaNotificacion getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaNotificacion categoria) {
        this.categoria = categoria;
    }

    public PrioridadNotificacion getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(PrioridadNotificacion prioridad) {
        this.prioridad = prioridad;
    }

    public SeveridadNotificacion getSeveridad() {
        return severidad;
    }

    public void setSeveridad(SeveridadNotificacion severidad) {
        this.severidad = severidad;
    }

    public String getEntidadTipo() {
        return entidadTipo;
    }

    public void setEntidadTipo(String entidadTipo) {
        this.entidadTipo = entidadTipo;
    }

    public Integer getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(Integer entidadId) {
        this.entidadId = entidadId;
    }

    public Map<String, Object> getDatosAdicionales() {
        return datosAdicionales;
    }

    public void setDatosAdicionales(Map<String, Object> datosAdicionales) {
        this.datosAdicionales = datosAdicionales;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }
}
