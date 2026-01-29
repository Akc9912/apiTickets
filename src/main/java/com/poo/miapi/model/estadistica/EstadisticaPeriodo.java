package com.poo.miapi.model.estadistica;

import com.poo.miapi.model.enums.PeriodoTipo;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "estadistica_periodo")
public class EstadisticaPeriodo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodo_tipo", nullable = false)
    private PeriodoTipo periodoTipo;

    @Column(nullable = false)
    private int anio;

    private int mes;
    private int semana;
    private int dia;
    private int trimestre;

    // Métricas de tickets
    @Column(name = "tickets_creados", nullable = false)
    private int ticketsCreados = 0;

    @Column(name = "tickets_asignados", nullable = false)
    private int ticketsAsignados = 0;

    @Column(name = "tickets_resueltos", nullable = false)
    private int ticketsResueltos = 0;

    @Column(name = "tickets_finalizados", nullable = false)
    private int ticketsFinalizados = 0;

    @Column(name = "tickets_reabiertos", nullable = false)
    private int ticketsReabiertos = 0;

    @Column(name = "tickets_pendientes", nullable = false)
    private int ticketsPendientes = 0;

    // Tiempos de resolución (en horas)
    @Column(name = "tiempo_promedio_asignacion", precision = 10, scale = 2)
    private BigDecimal tiempoPromedioAsignacion = BigDecimal.ZERO;

    @Column(name = "tiempo_promedio_resolucion", precision = 10, scale = 2)
    private BigDecimal tiempoPromedioResolucion = BigDecimal.ZERO;

    @Column(name = "tiempo_maximo_resolucion", precision = 10, scale = 2)
    private BigDecimal tiempoMaximoResolucion = BigDecimal.ZERO;

    @Column(name = "tiempo_minimo_resolucion", precision = 10, scale = 2)
    private BigDecimal tiempoMinimoResolucion = BigDecimal.ZERO;

    // Métricas de calidad
    @Column(name = "tickets_aprobados", nullable = false)
    private int ticketsAprobados = 0;

    @Column(name = "tickets_rechazados", nullable = false)
    private int ticketsRechazados = 0;

    @Column(name = "porcentaje_aprobacion", precision = 5, scale = 2)
    private BigDecimal porcentajeAprobacion = BigDecimal.ZERO;

    // Métricas de devoluciones
    @Column(name = "solicitudes_devolucion", nullable = false)
    private int solicitudesDevolucion = 0;

    @Column(name = "devoluciones_aprobadas", nullable = false)
    private int devolucionesAprobadas = 0;

    @Column(name = "devoluciones_rechazadas", nullable = false)
    private int devolucionesRechazadas = 0;

    @Column(name = "fecha_calculo")
    private LocalDateTime fechaCalculo = LocalDateTime.now();

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion = LocalDateTime.now();

    // Constructores
    public EstadisticaPeriodo() {
    }

    public EstadisticaPeriodo(PeriodoTipo periodoTipo, int anio) {
        this.periodoTipo = periodoTipo;
        this.anio = anio;
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

    // Métodos de utilidad
    public void incrementarTicketsCreados() {
        this.ticketsCreados++;
    }

    public void incrementarTicketsResueltos() {
        this.ticketsResueltos++;
    }

    public void incrementarTicketsFinalizados() {
        this.ticketsFinalizados++;
    }

    public void incrementarTicketsReabiertos() {
        this.ticketsReabiertos++;
    }

    public void calcularPorcentajeAprobacion() {
        int totalEvaluados = ticketsAprobados + ticketsRechazados;
        if (totalEvaluados > 0) {
            this.porcentajeAprobacion = BigDecimal.valueOf(ticketsAprobados * 100.0 / totalEvaluados)
                    .setScale(2, java.math.RoundingMode.HALF_UP);
        } else {
            this.porcentajeAprobacion = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.ultimaActualizacion = LocalDateTime.now();
    }
}
