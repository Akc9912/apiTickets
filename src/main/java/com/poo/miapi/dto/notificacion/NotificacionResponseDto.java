package com.poo.miapi.dto.notificacion;

import com.poo.miapi.model.enums.TipoNotificacion;
import com.poo.miapi.model.enums.CategoriaNotificacion;
import com.poo.miapi.model.enums.PrioridadNotificacion;
import com.poo.miapi.model.enums.SeveridadNotificacion;

import java.time.LocalDateTime;

public class NotificacionResponseDto {
    private Integer id;
    private Integer idUsuario;
    private String titulo;
    private String mensaje;
    private TipoNotificacion tipo;
    private CategoriaNotificacion categoria;
    private PrioridadNotificacion prioridad;
    private SeveridadNotificacion severidad;
    private Boolean leida;
    private Boolean archivada;
    private String entidadTipo;
    private Integer entidadId;
    private String icono;
    private String color;
    private Boolean mostrarBadge;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLectura;
    private Boolean expirada;

    public NotificacionResponseDto() {
    }

    public NotificacionResponseDto(Integer id, Integer idUsuario, String titulo, String mensaje,
            TipoNotificacion tipo, CategoriaNotificacion categoria,
            PrioridadNotificacion prioridad, SeveridadNotificacion severidad,
            Boolean leida, Boolean archivada, String entidadTipo, Integer entidadId,
            String icono, String color, Boolean mostrarBadge,
            LocalDateTime fechaCreacion, LocalDateTime fechaLectura, Boolean expirada) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.categoria = categoria;
        this.prioridad = prioridad;
        this.severidad = severidad;
        this.leida = leida;
        this.archivada = archivada;
        this.entidadTipo = entidadTipo;
        this.entidadId = entidadId;
        this.icono = icono;
        this.color = color;
        this.mostrarBadge = mostrarBadge;
        this.fechaCreacion = fechaCreacion;
        this.fechaLectura = fechaLectura;
        this.expirada = expirada;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
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

    public Boolean getLeida() {
        return leida;
    }

    public void setLeida(Boolean leida) {
        this.leida = leida;
    }

    public Boolean getArchivada() {
        return archivada;
    }

    public void setArchivada(Boolean archivada) {
        this.archivada = archivada;
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

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getMostrarBadge() {
        return mostrarBadge;
    }

    public void setMostrarBadge(Boolean mostrarBadge) {
        this.mostrarBadge = mostrarBadge;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaLectura() {
        return fechaLectura;
    }

    public void setFechaLectura(LocalDateTime fechaLectura) {
        this.fechaLectura = fechaLectura;
    }

    public Boolean getExpirada() {
        return expirada;
    }

    public void setExpirada(Boolean expirada) {
        this.expirada = expirada;
    }
}
