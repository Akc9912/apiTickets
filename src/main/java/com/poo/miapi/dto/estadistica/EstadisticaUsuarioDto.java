package com.poo.miapi.dto.estadistica;

import com.poo.miapi.model.enums.Rol;
import com.poo.miapi.model.enums.PeriodoTipo;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para estadísticas por usuario individual
 * Incluye métricas específicas según el rol
 */
public class EstadisticaUsuarioDto {

    private int id;
    private int usuarioId;
    private String nombreUsuario;
    private String apellidoUsuario;
    private String emailUsuario;
    private Rol tipoUsuario;
    private PeriodoTipo periodoTipo;
    private int anio;
    private Integer mes;
    private Integer dia;
    private Integer semana;
    private Integer trimestre;

    // Métricas generales
    private int ticketsCreados = 0;
    private int ticketsEvaluados = 0;
    private int solicitudesDevolucionCreadas = 0;
    private int solicitudesDevolucionProcesadas = 0;

    // Métricas específicas para Admins/SuperAdmins
    private int ticketsAsignados = 0;
    private int usuariosGestionados = 0;
    private int reportesGenerados = 0;

    // Métricas específicas para Trabajadores
    private int incidentesReportados = 0;
    private int evaluacionesRealizadas = 0;
    private int ticketsRechazadosPorUsuario = 0;

    // Tiempos y métricas de calidad
    private BigDecimal tiempoPromedioEvaluacion = BigDecimal.ZERO;
    private BigDecimal porcentajeAprobacion = BigDecimal.ZERO;

    // Métricas de actividad
    private int sesionesActivas = 0;
    private int tiempoConectadoMinutos = 0;
    private String tiempoConectadoFormateado; // "2h 30m"

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Constructors
    public EstadisticaUsuarioDto() {
    }

    // Constructor básico
    public EstadisticaUsuarioDto(int usuarioId, String nombreUsuario, String apellidoUsuario,
            String emailUsuario, Rol tipoUsuario, PeriodoTipo periodoTipo, int anio) {
        this.usuarioId = usuarioId;
        this.nombreUsuario = nombreUsuario;
        this.apellidoUsuario = apellidoUsuario;
        this.emailUsuario = emailUsuario;
        this.tipoUsuario = tipoUsuario;
        this.periodoTipo = periodoTipo;
        this.anio = anio;
    }

    // Métodos de utilidad
    public String getNombreCompleto() {
        return nombreUsuario + " " + apellidoUsuario;
    }

    public String getPeriodoFormateado() {
        switch (periodoTipo) {
            case DIARIO:
                return String.format("%02d/%02d/%d", dia, mes, anio);
            case SEMANAL:
                return String.format("Semana %d/%d", semana, anio);
            case MENSUAL:
                return String.format("%02d/%d", mes, anio);
            case TRIMESTRAL:
                return String.format("Q%d/%d", trimestre, anio);
            case ANUAL:
                return String.valueOf(anio);
            default:
                return "";
        }
    }

    public void formatearTiempoConectado() {
        if (tiempoConectadoMinutos <= 0) {
            this.tiempoConectadoFormateado = "0m";
            return;
        }

        int horas = tiempoConectadoMinutos / 60;
        int minutos = tiempoConectadoMinutos % 60;

        if (horas > 0) {
            this.tiempoConectadoFormateado = String.format("%dh %dm", horas, minutos);
        } else {
            this.tiempoConectadoFormateado = String.format("%dm", minutos);
        }
    }

    public boolean esPeriodoActual() {
        LocalDateTime ahora = LocalDateTime.now();
        int anioActual = ahora.getYear();
        int mesActual = ahora.getMonthValue();
        int diaActual = ahora.getDayOfMonth();

        if (this.anio != anioActual)
            return false;

        switch (periodoTipo) {
            case ANUAL:
                return true;
            case MENSUAL:
                return this.mes == mesActual;
            case DIARIO:
                return this.mes == mesActual && this.dia == diaActual;
            default:
                return false;
        }
    }

    // Métricas calculadas
    public double getProductividad() {
        if (tiempoConectadoMinutos <= 0)
            return 0;
        return (double) ticketsEvaluados / (tiempoConectadoMinutos / 60.0);
    }

    public String getNivelActividad() {
        if (tiempoConectadoMinutos <= 60)
            return "Baja";
        if (tiempoConectadoMinutos <= 300)
            return "Media";
        return "Alta";
    }

    public String getRendimiento() {
        if (porcentajeAprobacion.compareTo(BigDecimal.valueOf(90)) >= 0)
            return "Excelente";
        if (porcentajeAprobacion.compareTo(BigDecimal.valueOf(70)) >= 0)
            return "Bueno";
        if (porcentajeAprobacion.compareTo(BigDecimal.valueOf(50)) >= 0)
            return "Regular";
        return "Bajo";
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getApellidoUsuario() {
        return apellidoUsuario;
    }

    public void setApellidoUsuario(String apellidoUsuario) {
        this.apellidoUsuario = apellidoUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
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
        formatearTiempoConectado();
    }

    public String getTiempoConectadoFormateado() {
        return tiempoConectadoFormateado;
    }

    public void setTiempoConectadoFormateado(String tiempoConectadoFormateado) {
        this.tiempoConectadoFormateado = tiempoConectadoFormateado;
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
