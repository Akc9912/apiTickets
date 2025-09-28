package com.poo.miapi.model.notificacion;

import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.model.enums.TipoNotificacion;
import com.poo.miapi.model.enums.CategoriaNotificacion;
import com.poo.miapi.model.enums.PrioridadNotificacion;
import com.poo.miapi.model.enums.SeveridadNotificacion;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificacion tipo = TipoNotificacion.INFO_GENERAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaNotificacion categoria = CategoriaNotificacion.SISTEMA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadNotificacion prioridad = PrioridadNotificacion.MEDIA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeveridadNotificacion severidad = SeveridadNotificacion.INFO;

    @Column(nullable = false)
    private Boolean leida = false;

    @Column(nullable = false)
    private Boolean archivada = false;

    @Column(nullable = false)
    private Boolean eliminada = false;

    @Column(name = "entidad_tipo", length = 50)
    private String entidadTipo;

    @Column(name = "entidad_id")
    private Integer entidadId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "datos_adicionales", columnDefinition = "JSON")
    private Map<String, Object> datosAdicionales;

    @Column(name = "accion_automatica", length = 100)
    private String accionAutomatica;

    @Column(name = "url_accion", length = 500)
    private String urlAccion;

    @Column(length = 50)
    private String icono = "info";

    @Column(length = 20)
    private String color = "blue";

    @Column(name = "mostrar_badge", nullable = false)
    private Boolean mostrarBadge = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;

    @Column(name = "fecha_archivado")
    private LocalDateTime fechaArchivado;

    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "fecha_eliminacion")
    private LocalDateTime fechaEliminacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eliminado_por")
    private Usuario eliminadoPor;

    // Constructores
    public Notificacion() {
    }

    public Notificacion(Usuario usuario, String titulo, String mensaje, TipoNotificacion tipo,
            CategoriaNotificacion categoria) {
        this.usuario = usuario;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.categoria = categoria;
    }

    public Notificacion(Usuario usuario, String titulo, String mensaje, TipoNotificacion tipo,
            CategoriaNotificacion categoria, PrioridadNotificacion prioridad, SeveridadNotificacion severidad) {
        this(usuario, titulo, mensaje, tipo, categoria);
        this.prioridad = prioridad;
        this.severidad = severidad;
    }

    // Métodos de utilidad
    public boolean isExpirada() {
        return fechaExpiracion != null && fechaExpiracion.isBefore(LocalDateTime.now());
    }

    public void marcarComoLeida() {
        this.leida = true;
        this.fechaLectura = LocalDateTime.now();
    }

    public void archivar() {
        this.archivada = true;
        this.fechaArchivado = LocalDateTime.now();
    }

    public void eliminar(Usuario usuario) {
        this.eliminada = true;
        this.fechaEliminacion = LocalDateTime.now();
        this.eliminadoPor = usuario;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
        if (leida && this.fechaLectura == null) {
            this.fechaLectura = LocalDateTime.now();
        }
    }

    public Boolean getArchivada() {
        return archivada;
    }

    public void setArchivada(Boolean archivada) {
        this.archivada = archivada;
        if (archivada && this.fechaArchivado == null) {
            this.fechaArchivado = LocalDateTime.now();
        }
    }

    public Boolean getEliminada() {
        return eliminada;
    }

    public void setEliminada(Boolean eliminada) {
        this.eliminada = eliminada;
        if (eliminada && this.fechaEliminacion == null) {
            this.fechaEliminacion = LocalDateTime.now();
        }
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

    public String getAccionAutomatica() {
        return accionAutomatica;
    }

    public void setAccionAutomatica(String accionAutomatica) {
        this.accionAutomatica = accionAutomatica;
    }

    public String getUrlAccion() {
        return urlAccion;
    }

    public void setUrlAccion(String urlAccion) {
        this.urlAccion = urlAccion;
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

    public LocalDateTime getFechaArchivado() {
        return fechaArchivado;
    }

    public void setFechaArchivado(LocalDateTime fechaArchivado) {
        this.fechaArchivado = fechaArchivado;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public Usuario getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(Usuario creadoPor) {
        this.creadoPor = creadoPor;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }

    public void setIpOrigen(String ipOrigen) {
        this.ipOrigen = ipOrigen;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getFechaEliminacion() {
        return fechaEliminacion;
    }

    public void setFechaEliminacion(LocalDateTime fechaEliminacion) {
        this.fechaEliminacion = fechaEliminacion;
    }

    public Usuario getEliminadoPor() {
        return eliminadoPor;
    }

    public void setEliminadoPor(Usuario eliminadoPor) {
        this.eliminadoPor = eliminadoPor;
    }
}
