package com.poo.miapi.model.estadistica;

import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.enums.PeriodoTipo;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "estadistica_tecnico")
public class EstadisticaTecnico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tecnico", nullable = false)
    private Tecnico tecnico;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodo_tipo", nullable = false)
    private PeriodoTipo periodoTipo;

    @Column(nullable = false)
    private int anio;

    private int mes;
    private int semana;
    private int dia;
    private int trimestre;

    // Productividad
    @Column(name = "tickets_asignados", nullable = false)
    private int ticketsAsignados = 0;

    @Column(name = "tickets_completados", nullable = false)
    private int ticketsCompletados = 0;

    @Column(name = "tickets_aprobados", nullable = false)
    private int ticketsAprobados = 0;

    @Column(name = "tickets_rechazados", nullable = false)
    private int ticketsRechazados = 0;

    // Tiempos promedio
    @Column(name = "tiempo_promedio_resolucion", precision = 10, scale = 2)
    private BigDecimal tiempoPromedioResolucion = BigDecimal.ZERO;

    @Column(name = "tickets_dentro_sla", nullable = false)
    private int ticketsDentroSla = 0;

    // Calidad
    @Column(name = "porcentaje_aprobacion", precision = 5, scale = 2)
    private BigDecimal porcentajeAprobacion = BigDecimal.ZERO;

    @Column(name = "marcas_recibidas", nullable = false)
    private int marcasRecibidas = 0;

    @Column(name = "fallas_registradas", nullable = false)
    private int fallasRegistradas = 0;

    // Devoluciones
    @Column(name = "solicitudes_devolucion", nullable = false)
    private int solicitudesDevolucion = 0;

    @Column(name = "devoluciones_aprobadas", nullable = false)
    private int devolucionesAprobadas = 0;

    // Ranking relativo
    @Column(name = "ranking_productividad")
    private int rankingProductividad;

    @Column(name = "ranking_calidad")
    private int rankingCalidad;

    @Column(name = "fecha_calculo")
    private LocalDateTime fechaCalculo = LocalDateTime.now();

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion = LocalDateTime.now();

    // Constructores
    public EstadisticaTecnico() {
    }

    public EstadisticaTecnico(Tecnico tecnico, PeriodoTipo periodoTipo, int anio) {
        this.tecnico = tecnico;
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

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
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

    public int getTicketsAsignados() {
        return ticketsAsignados;
    }

    public void setTicketsAsignados(int ticketsAsignados) {
        this.ticketsAsignados = ticketsAsignados;
    }

    public int getTicketsCompletados() {
        return ticketsCompletados;
    }

    public void setTicketsCompletados(int ticketsCompletados) {
        this.ticketsCompletados = ticketsCompletados;
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

    public BigDecimal getTiempoPromedioResolucion() {
        return tiempoPromedioResolucion;
    }

    public void setTiempoPromedioResolucion(BigDecimal tiempoPromedioResolucion) {
        this.tiempoPromedioResolucion = tiempoPromedioResolucion;
    }

    public int getTicketsDentroSla() {
        return ticketsDentroSla;
    }

    public void setTicketsDentroSla(int ticketsDentroSla) {
        this.ticketsDentroSla = ticketsDentroSla;
    }

    public BigDecimal getPorcentajeAprobacion() {
        return porcentajeAprobacion;
    }

    public void setPorcentajeAprobacion(BigDecimal porcentajeAprobacion) {
        this.porcentajeAprobacion = porcentajeAprobacion;
    }

    public int getMarcasRecibidas() {
        return marcasRecibidas;
    }

    public void setMarcasRecibidas(int marcasRecibidas) {
        this.marcasRecibidas = marcasRecibidas;
    }

    public int getFallasRegistradas() {
        return fallasRegistradas;
    }

    public void setFallasRegistradas(int fallasRegistradas) {
        this.fallasRegistradas = fallasRegistradas;
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

    public int getRankingProductividad() {
        return rankingProductividad;
    }

    public void setRankingProductividad(int rankingProductividad) {
        this.rankingProductividad = rankingProductividad;
    }

    public int getRankingCalidad() {
        return rankingCalidad;
    }

    public void setRankingCalidad(int rankingCalidad) {
        this.rankingCalidad = rankingCalidad;
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
    public void calcularPorcentajeAprobacion() {
        int totalEvaluados = ticketsAprobados + ticketsRechazados;
        if (totalEvaluados > 0) {
            this.porcentajeAprobacion = BigDecimal.valueOf(ticketsAprobados * 100.0 / totalEvaluados)
                    .setScale(2, java.math.RoundingMode.HALF_UP);
        } else {
            this.porcentajeAprobacion = BigDecimal.ZERO;
        }
    }

    public BigDecimal calcularRatioProductividad() {
        if (ticketsAsignados > 0) {
            return BigDecimal.valueOf(ticketsCompletados * 100.0 / ticketsAsignados)
                    .setScale(2, java.math.RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    @PreUpdate
    public void preUpdate() {
        this.ultimaActualizacion = LocalDateTime.now();
    }
}
