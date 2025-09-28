package com.poo.miapi.model.auditoria;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.model.enums.Rol;
import com.poo.miapi.model.enums.AccionAuditoria;
import com.poo.miapi.model.enums.CategoriaAuditoria;
import com.poo.miapi.model.enums.SeveridadAuditoria;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
public class Auditoria implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "nombre_usuario", nullable = false, length = 100)
    private String nombreUsuario;

    @Column(name = "email_usuario", length = 100)
    private String emailUsuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_usuario")
    private Rol rolUsuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccionAuditoria accion;

    @Column(name = "entidad_tipo", nullable = false, length = 50)
    private String entidadTipo;

    @Column(name = "entidad_id")
    private Integer entidadId;

    @Column(name = "detalle_accion", columnDefinition = "TEXT")
    private String detalleAccion;

    @JsonRawValue
    @Column(name = "valores_anteriores", columnDefinition = "JSON")
    private String valoresAnteriores;

    @JsonRawValue
    @Column(name = "valores_nuevos", columnDefinition = "JSON")
    private String valoresNuevos;

    @Column(name = "direccion_ip", length = 45)
    private String direccionIp;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "sesion_id")
    private String sesionId;

    @Column(name = "fecha_accion", nullable = false)
    private LocalDateTime fechaAccion;

    @Column(name = "fecha_procesamiento")
    private LocalDateTime fechaProcesamiento;

    @Enumerated(EnumType.STRING)
    private CategoriaAuditoria categoria;

    @Enumerated(EnumType.STRING)
    private SeveridadAuditoria severidad;

    @Column(name = "hash_integridad", length = 64)
    private String hashIntegridad;

    @Column(name = "retencion_hasta")
    private LocalDateTime retencionHasta;

    // Constructores
    public Auditoria() {
        this.fechaAccion = LocalDateTime.now();
        this.categoria = CategoriaAuditoria.BUSINESS;
        this.severidad = SeveridadAuditoria.MEDIUM;
    }

    public Auditoria(Usuario usuario, AccionAuditoria accion, String entidadTipo, Integer entidadId,
            String detalleAccion) {
        this();
        this.usuario = usuario;
        if (usuario != null) {
            this.nombreUsuario = usuario.getNombre() + " " + usuario.getApellido();
            this.emailUsuario = usuario.getEmail();
            this.rolUsuario = usuario.getRol();
        }
        this.accion = accion;
        this.entidadTipo = entidadTipo;
        this.entidadId = entidadId;
        this.detalleAccion = detalleAccion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public Rol getRolUsuario() {
        return rolUsuario;
    }

    public void setRolUsuario(Rol rolUsuario) {
        this.rolUsuario = rolUsuario;
    }

    public AccionAuditoria getAccion() {
        return accion;
    }

    public void setAccion(AccionAuditoria accion) {
        this.accion = accion;
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

    public String getDetalleAccion() {
        return detalleAccion;
    }

    public void setDetalleAccion(String detalleAccion) {
        this.detalleAccion = detalleAccion;
    }

    public String getValoresAnteriores() {
        return valoresAnteriores;
    }

    public void setValoresAnteriores(String valoresAnteriores) {
        this.valoresAnteriores = valoresAnteriores;
    }

    public String getValoresNuevos() {
        return valoresNuevos;
    }

    public void setValoresNuevos(String valoresNuevos) {
        this.valoresNuevos = valoresNuevos;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getSesionId() {
        return sesionId;
    }

    public void setSesionId(String sesionId) {
        this.sesionId = sesionId;
    }

    public LocalDateTime getFechaAccion() {
        return fechaAccion;
    }

    public void setFechaAccion(LocalDateTime fechaAccion) {
        this.fechaAccion = fechaAccion;
    }

    public LocalDateTime getFechaProcesamiento() {
        return fechaProcesamiento;
    }

    public void setFechaProcesamiento(LocalDateTime fechaProcesamiento) {
        this.fechaProcesamiento = fechaProcesamiento;
    }

    public CategoriaAuditoria getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaAuditoria categoria) {
        this.categoria = categoria;
    }

    public SeveridadAuditoria getSeveridad() {
        return severidad;
    }

    public void setSeveridad(SeveridadAuditoria severidad) {
        this.severidad = severidad;
    }

    public String getHashIntegridad() {
        return hashIntegridad;
    }

    public void setHashIntegridad(String hashIntegridad) {
        this.hashIntegridad = hashIntegridad;
    }

    public LocalDateTime getRetencionHasta() {
        return retencionHasta;
    }

    public void setRetencionHasta(LocalDateTime retencionHasta) {
        this.retencionHasta = retencionHasta;
    }
}
