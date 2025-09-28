package com.poo.miapi.dto.estadistica;

import com.poo.miapi.model.enums.PeriodoTipo;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EstadisticaPeriodoDto {

    private int id;
    private PeriodoTipo periodoTipo;
    private int anio;
    private int mes;
    private int semana;
    private int dia;
    private int trimestre;

    // Métricas de tickets
    private int ticketsCreados;
    private int ticketsAsignados;
    private int ticketsResueltos;
    private int ticketsFinalizados;
    private int ticketsReabiertos;
    private int ticketsPendientes;

    // Tiempos de resolución
    private BigDecimal tiempoPromedioAsignacion;
    private BigDecimal tiempoPromedioResolucion;
    private BigDecimal tiempoMaximoResolucion;
    private BigDecimal tiempoMinimoResolucion;

    // Métricas de calidad
    private int ticketsAprobados;
    private int ticketsRechazados;
    private BigDecimal porcentajeAprobacion;

    // Métricas de devoluciones
    private int solicitudesDevolucion;
    private int devolucionesAprobadas;
    private int devolucionesRechazadas;

    private LocalDateTime fechaCalculo;
    private LocalDateTime ultimaActualizacion;

    // Constructores
    public EstadisticaPeriodoDto() {
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PeriodoTipo getPeriodoTipo() {
        return periodoTipo;
    }

    public void setPeriodoTipo(PeriodoTipo periodoTipo) {
        this.periodoTipo = periodoTipo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getSemana() {
        return semana;
    }

    public void setSemana(int semana) {
        this.semana = semana;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getTrimestre() {
        return trimestre;
    }

    public void setTrimestre(int trimestre) {
        this.trimestre = trimestre;
    }

    public int getTicketsCreados() {
        return ticketsCreados;
    }

    public void setTicketsCreados(int ticketsCreados) {
        this.ticketsCreados = ticketsCreados;
    }

    public int getTicketsAsignados() {
        return ticketsAsignados;
    }

    public void setTicketsAsignados(int ticketsAsignados) {
        this.ticketsAsignados = ticketsAsignados;
    }

    public int getTicketsResueltos() {
        return ticketsResueltos;
    }

    public void setTicketsResueltos(int ticketsResueltos) {
        this.ticketsResueltos = ticketsResueltos;
    }

    public int getTicketsFinalizados() {
        return ticketsFinalizados;
    }

    public void setTicketsFinalizados(int ticketsFinalizados) {
        this.ticketsFinalizados = ticketsFinalizados;
    }

    public int getTicketsReabiertos() {
        return ticketsReabiertos;
    }

    public void setTicketsReabiertos(int ticketsReabiertos) {
        this.ticketsReabiertos = ticketsReabiertos;
    }

    public int getTicketsPendientes() {
        return ticketsPendientes;
    }

    public void setTicketsPendientes(int ticketsPendientes) {
        this.ticketsPendientes = ticketsPendientes;
    }

    public BigDecimal getTiempoPromedioAsignacion() {
        return tiempoPromedioAsignacion;
    }

    public void setTiempoPromedioAsignacion(BigDecimal tiempoPromedioAsignacion) {
        this.tiempoPromedioAsignacion = tiempoPromedioAsignacion;
    }

    public BigDecimal getTiempoPromedioResolucion() {
        return tiempoPromedioResolucion;
    }

    public void setTiempoPromedioResolucion(BigDecimal tiempoPromedioResolucion) {
        this.tiempoPromedioResolucion = tiempoPromedioResolucion;
    }

    public BigDecimal getTiempoMaximoResolucion() {
        return tiempoMaximoResolucion;
    }

    public void setTiempoMaximoResolucion(BigDecimal tiempoMaximoResolucion) {
        this.tiempoMaximoResolucion = tiempoMaximoResolucion;
    }

    public BigDecimal getTiempoMinimoResolucion() {
        return tiempoMinimoResolucion;
    }

    public void setTiempoMinimoResolucion(BigDecimal tiempoMinimoResolucion) {
        this.tiempoMinimoResolucion = tiempoMinimoResolucion;
    }

    public int getTicketsAprobados() {
        return ticketsAprobados;
    }

    public void setTicketsAprobados(int ticketsAprobados) {
        this.ticketsAprobados = ticketsAprobados;
    }

    public int getTicketsRechazados() {
        return ticketsRechazados;
    }

    public void setTicketsRechazados(int ticketsRechazados) {
        this.ticketsRechazados = ticketsRechazados;
    }

    public BigDecimal getPorcentajeAprobacion() {
        return porcentajeAprobacion;
    }

    public void setPorcentajeAprobacion(BigDecimal porcentajeAprobacion) {
        this.porcentajeAprobacion = porcentajeAprobacion;
    }

    public int getSolicitudesDevolucion() {
        return solicitudesDevolucion;
    }

    public void setSolicitudesDevolucion(int solicitudesDevolucion) {
        this.solicitudesDevolucion = solicitudesDevolucion;
    }

    public int getDevolucionesAprobadas() {
        return devolucionesAprobadas;
    }

    public void setDevolucionesAprobadas(int devolucionesAprobadas) {
        this.devolucionesAprobadas = devolucionesAprobadas;
    }

    public int getDevolucionesRechazadas() {
        return devolucionesRechazadas;
    }

    public void setDevolucionesRechazadas(int devolucionesRechazadas) {
        this.devolucionesRechazadas = devolucionesRechazadas;
    }

    public LocalDateTime getFechaCalculo() {
        return fechaCalculo;
    }

    public void setFechaCalculo(LocalDateTime fechaCalculo) {
        this.fechaCalculo = fechaCalculo;
    }

    public LocalDateTime getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    // Métodos de utilidad para el frontend
    public double getRatioCompletados() {
        if (ticketsCreados > 0) {
            return (double) ticketsFinalizados / ticketsCreados * 100;
        }
        return 0.0;
    }

    public double getRatioCalidad() {
        int totalEvaluados = ticketsAprobados + ticketsRechazados;
        if (totalEvaluados > 0) {
            return (double) ticketsAprobados / totalEvaluados * 100;
        }
        return 0.0;
    }

    public String getPeriodoDescripcion() {
        if (periodoTipo == null)
            return "";

        switch (periodoTipo) {
            case DIARIO:
                return String.format("%d/%d/%d", dia, mes, anio);
            case SEMANAL:
                return String.format("Semana %d - %d", semana, anio);
            case MENSUAL:
                return String.format("%d/%d", mes, anio);
            case TRIMESTRAL:
                return String.format("Q%d %d", trimestre, anio);
            case ANUAL:
                return String.valueOf(anio);
            default:
                return "";
        }
    }
}
