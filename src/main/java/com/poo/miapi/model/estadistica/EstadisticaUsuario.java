package com.poo.miapi.model.estadistica;

import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.model.enums.PeriodoTipo;
import com.poo.miapi.model.enums.Rol;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.math.RoundingMode;

/**
 * Estadísticas por tipo de usuario (Admin, SuperAdmin, Trabajador)
 * Métricas específicas según el rol
 */
@Entity
@Table(name = "estadistica_usuario", indexes = {
        @Index(name = "idx_usuario_periodo", columnList = "usuario_id, periodo_tipo, anio, mes"),
        @Index(name = "idx_tipo_usuario_periodo", columnList = "tipo_usuario, periodo_tipo, anio, mes")
})
public class EstadisticaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false)
    private Rol tipoUsuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodo_tipo", nullable = false)
    private PeriodoTipo periodoTipo;

    @Column(nullable = false)
    private int anio;

    private Integer mes;
    private Integer dia;
    private Integer semana;
    private Integer trimestre;

    // Métricas generales
    @Column(name = "tickets_creados", nullable = false, columnDefinition = "int default 0")
    private int ticketsCreados = 0;

    @Column(name = "tickets_evaluados", nullable = false, columnDefinition = "int default 0")
    private int ticketsEvaluados = 0;

    @Column(name = "solicitudes_devolucion_creadas", nullable = false, columnDefinition = "int default 0")
    private int solicitudesDevolucionCreadas = 0;

    @Column(name = "solicitudes_devolucion_procesadas", nullable = false, columnDefinition = "int default 0")
    private int solicitudesDevolucionProcesadas = 0;

    // Métricas específicas para Admins/SuperAdmins
    @Column(name = "tickets_asignados", nullable = false, columnDefinition = "int default 0")
    private int ticketsAsignados = 0;

    @Column(name = "usuarios_gestionados", nullable = false, columnDefinition = "int default 0")
    private int usuariosGestionados = 0;

    @Column(name = "reportes_generados", nullable = false, columnDefinition = "int default 0")
    private int reportesGenerados = 0;

    // Métricas específicas para Trabajadores
    @Column(name = "incidentes_reportados", nullable = false, columnDefinition = "int default 0")
    private int incidentesReportados = 0;

    @Column(name = "evaluaciones_realizadas", nullable = false, columnDefinition = "int default 0")
    private int evaluacionesRealizadas = 0;

    @Column(name = "tickets_rechazados_por_usuario", nullable = false, columnDefinition = "int default 0")
    private int ticketsRechazadosPorUsuario = 0;

    // Tiempos y métricas de calidad
    @Column(name = "tiempo_promedio_evaluacion", precision = 10, scale = 2)
    private BigDecimal tiempoPromedioEvaluacion = BigDecimal.ZERO;

    @Column(name = "porcentaje_aprobacion", precision = 5, scale = 2)
    private BigDecimal porcentajeAprobacion = BigDecimal.ZERO;

    // Métricas de actividad
    @Column(name = "sesiones_activas", nullable = false, columnDefinition = "int default 0")
    private int sesionesActivas = 0;

    @Column(name = "tiempo_conectado_minutos", nullable = false, columnDefinition = "int default 0")
    private int tiempoConectadoMinutos = 0;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Constructors
    public EstadisticaUsuario() {
    }

    public EstadisticaUsuario(Usuario usuario, PeriodoTipo periodoTipo, int anio) {
        this.usuario = usuario;
        this.tipoUsuario = usuario.getRol();
        this.periodoTipo = periodoTipo;
        this.anio = anio;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Métodos de cálculo
    public void calcularPorcentajeAprobacion() {
        int totalEvaluados = this.ticketsEvaluados;
        if (totalEvaluados > 0) {
            int aprobados = totalEvaluados - this.ticketsRechazadosPorUsuario;
            this.porcentajeAprobacion = BigDecimal.valueOf(aprobados)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalEvaluados), 2, RoundingMode.HALF_UP);
        }
    }

    public void actualizarFechaModificacion() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Métodos de incremento
    public void incrementarTicketsCreados() {
        this.ticketsCreados++;
        actualizarFechaModificacion();
    }

    public void incrementarTicketsEvaluados() {
        this.ticketsEvaluados++;
        actualizarFechaModificacion();
    }

    public void incrementarSolicitudesDevolucion() {
        this.solicitudesDevolucionCreadas++;
        actualizarFechaModificacion();
    }

    public void incrementarIncidentesReportados() {
        this.incidentesReportados++;
        actualizarFechaModificacion();
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Rol getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(Rol tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
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

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getDia() {
        return dia;
    }

    public void setDia(Integer dia) {
        this.dia = dia;
    }

    public Integer getSemana() {
        return semana;
    }

    public void setSemana(Integer semana) {
        this.semana = semana;
    }

    public Integer getTrimestre() {
        return trimestre;
    }

    public void setTrimestre(Integer trimestre) {
        this.trimestre = trimestre;
    }

    public int getTicketsCreados() {
        return ticketsCreados;
    }

    public void setTicketsCreados(int ticketsCreados) {
        this.ticketsCreados = ticketsCreados;
    }

    public int getTicketsEvaluados() {
        return ticketsEvaluados;
    }

    public void setTicketsEvaluados(int ticketsEvaluados) {
        this.ticketsEvaluados = ticketsEvaluados;
    }

    public int getSolicitudesDevolucionCreadas() {
        return solicitudesDevolucionCreadas;
    }

    public void setSolicitudesDevolucionCreadas(int solicitudesDevolucionCreadas) {
        this.solicitudesDevolucionCreadas = solicitudesDevolucionCreadas;
    }

    public int getSolicitudesDevolucionProcesadas() {
        return solicitudesDevolucionProcesadas;
    }

    public void setSolicitudesDevolucionProcesadas(int solicitudesDevolucionProcesadas) {
        this.solicitudesDevolucionProcesadas = solicitudesDevolucionProcesadas;
    }

    public int getTicketsAsignados() {
        return ticketsAsignados;
    }

    public void setTicketsAsignados(int ticketsAsignados) {
        this.ticketsAsignados = ticketsAsignados;
    }

    public int getUsuariosGestionados() {
        return usuariosGestionados;
    }

    public void setUsuariosGestionados(int usuariosGestionados) {
        this.usuariosGestionados = usuariosGestionados;
    }

    public int getReportesGenerados() {
        return reportesGenerados;
    }

    public void setReportesGenerados(int reportesGenerados) {
        this.reportesGenerados = reportesGenerados;
    }

    public int getIncidentesReportados() {
        return incidentesReportados;
    }

    public void setIncidentesReportados(int incidentesReportados) {
        this.incidentesReportados = incidentesReportados;
    }

    public int getEvaluacionesRealizadas() {
        return evaluacionesRealizadas;
    }

    public void setEvaluacionesRealizadas(int evaluacionesRealizadas) {
        this.evaluacionesRealizadas = evaluacionesRealizadas;
    }

    public int getTicketsRechazadosPorUsuario() {
        return ticketsRechazadosPorUsuario;
    }

    public void setTicketsRechazadosPorUsuario(int ticketsRechazadosPorUsuario) {
        this.ticketsRechazadosPorUsuario = ticketsRechazadosPorUsuario;
    }

    public BigDecimal getTiempoPromedioEvaluacion() {
        return tiempoPromedioEvaluacion;
    }

    public void setTiempoPromedioEvaluacion(BigDecimal tiempoPromedioEvaluacion) {
        this.tiempoPromedioEvaluacion = tiempoPromedioEvaluacion;
    }

    public BigDecimal getPorcentajeAprobacion() {
        return porcentajeAprobacion;
    }

    public void setPorcentajeAprobacion(BigDecimal porcentajeAprobacion) {
        this.porcentajeAprobacion = porcentajeAprobacion;
    }

    public int getSesionesActivas() {
        return sesionesActivas;
    }

    public void setSesionesActivas(int sesionesActivas) {
        this.sesionesActivas = sesionesActivas;
    }

    public int getTiempoConectadoMinutos() {
        return tiempoConectadoMinutos;
    }

    public void setTiempoConectadoMinutos(int tiempoConectadoMinutos) {
        this.tiempoConectadoMinutos = tiempoConectadoMinutos;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
