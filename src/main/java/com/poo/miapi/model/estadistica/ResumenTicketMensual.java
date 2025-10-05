package com.poo.miapi.model.estadistica;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "resumen_ticket_mensual")
@IdClass(ResumenTicketMensualId.class)
public class ResumenTicketMensual implements Serializable {

    @Id
    @Column(nullable = false)
    private Integer anio;

    @Id
    @Column(nullable = false)
    private Integer mes;

    @Column(name = "total_creados", nullable = false)
    private Integer totalCreados = 0;

    @Column(name = "total_resueltos", nullable = false)
    private Integer totalResueltos = 0;

    @Column(name = "total_reabiertos", nullable = false)
    private Integer totalReabiertos = 0;

    @Column(name = "promedio_tiempo_resolucion", precision = 10, scale = 2)
    private BigDecimal promedioTiempoResolucion;

    // Constructores
    public ResumenTicketMensual() {
    }

    public ResumenTicketMensual(Integer anio, Integer mes) {
        this.anio = anio;
        this.mes = mes;
        this.totalCreados = 0;
        this.totalResueltos = 0;
        this.totalReabiertos = 0;
    }

    // Getters y Setters
    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getTotalCreados() {
        return totalCreados;
    }

    public void setTotalCreados(Integer totalCreados) {
        this.totalCreados = totalCreados;
    }

    public Integer getTotalResueltos() {
        return totalResueltos;
    }

    public void setTotalResueltos(Integer totalResueltos) {
        this.totalResueltos = totalResueltos;
    }

    public Integer getTotalReabiertos() {
        return totalReabiertos;
    }

    public void setTotalReabiertos(Integer totalReabiertos) {
        this.totalReabiertos = totalReabiertos;
    }

    public BigDecimal getPromedioTiempoResolucion() {
        return promedioTiempoResolucion;
    }

    public void setPromedioTiempoResolucion(BigDecimal promedioTiempoResolucion) {
        this.promedioTiempoResolucion = promedioTiempoResolucion;
    }

    @Override
    public String toString() {
        return "ResumenTicketMensual{" +
                "anio=" + anio +
                ", mes=" + mes +
                ", totalCreados=" + totalCreados +
                ", totalResueltos=" + totalResueltos +
                ", totalReabiertos=" + totalReabiertos +
                ", promedioTiempoResolucion=" + promedioTiempoResolucion +
                '}';
    }
}
