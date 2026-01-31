package com.poo.miapi.dto.auditoria;

import com.poo.miapi.model.enums.AccionAuditoria;
import com.poo.miapi.model.enums.CategoriaAuditoria;
import com.poo.miapi.model.enums.Rol;
import com.poo.miapi.model.enums.SeveridadAuditoria;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AuditoriaResponseDto implements Serializable {

    private Long id;
    private Integer idUsuario;
    private String nombreUsuario;
    private String emailUsuario;
    private Rol rolUsuario;
    private AccionAuditoria accion;
    private String entidadTipo;
    private Integer entidadId;
    private String detalleAccion;
    private String valoresAnteriores;
    private String valoresNuevos;
    private String direccionIp;
    private String userAgent;
    private LocalDateTime fechaAccion;
    private CategoriaAuditoria categoria;
    private SeveridadAuditoria severidad;

    // Constructor vacío
    public AuditoriaResponseDto() {
    }

    // Constructor con parámetros principales
    public AuditoriaResponseDto(Long id, String nombreUsuario, AccionAuditoria accion,
            String entidadTipo, Integer entidadId, String detalleAccion,
            LocalDateTime fechaAccion, CategoriaAuditoria categoria,
            SeveridadAuditoria severidad) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.accion = accion;
        this.entidadTipo = entidadTipo;
        this.entidadId = entidadId;
        this.detalleAccion = detalleAccion;
        this.fechaAccion = fechaAccion;
        this.categoria = categoria;
        this.severidad = severidad;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
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

    public LocalDateTime getFechaAccion() {
        return fechaAccion;
    }

    public void setFechaAccion(LocalDateTime fechaAccion) {
        this.fechaAccion = fechaAccion;
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

    @Override
    public String toString() {
        return "AuditoriaResponseDto{" +
                "id=" + id +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", accion=" + accion +
                ", entidadTipo='" + entidadTipo + '\'' +
                ", entidadId=" + entidadId +
                ", fechaAccion=" + fechaAccion +
                ", categoria=" + categoria +
                ", severidad=" + severidad +
                '}';
    }
}
