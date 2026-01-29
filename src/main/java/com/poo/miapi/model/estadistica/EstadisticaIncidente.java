package com.poo.miapi.model.estadistica;

import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.model.enums.PeriodoTipo;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Estadísticas específicas de incidentes técnicos
 * Métricas de resolución, tipos, severidad
 */
@Entity
@Table(name = "estadistica_incidente", indexes = {
        @Index(name = "idx_incidente_periodo", columnList = "periodo_tipo, anio, mes"),
        @Index(name = "idx_incidente_tecnico", columnList = "tecnico_id, anio, mes"),
        @Index(name = "idx_incidente_reportador", columnList = "usuario_reportador_id, anio, mes")
})
public class EstadisticaIncidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodo_tipo", nullable = false)
    private PeriodoTipo periodoTipo;

    @Column(nullable = false)
    private int anio;

    private Integer mes;
    private Integer dia;
    private Integer semana;
    private Integer trimestre;

    // Relaciones opcionales para estadísticas específicas
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id")
    private Tecnico tecnico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_reportador_id")
    private Usuario usuarioReportador;

    // Métricas de incidentes
    @Column(name = "incidentes_reportados", nullable = false, columnDefinition = "int default 0")
    private int incidentesReportados = 0;

    @Column(name = "incidentes_resueltos", nullable = false, columnDefinition = "int default 0")
    private int incidentesResueltos = 0;

    @Column(name = "incidentes_en_progreso", nullable = false, columnDefinition = "int default 0")
    private int incidentesEnProgreso = 0;

    @Column(name = "incidentes_escalados", nullable = false, columnDefinition = "int default 0")
    private int incidentesEscalados = 0;

    // Métricas por severidad
    @Column(name = "incidentes_criticos", nullable = false, columnDefinition = "int default 0")
    private int incidentesCriticos = 0;

    @Column(name = "incidentes_altos", nullable = false, columnDefinition = "int default 0")
    private int incidentesAltos = 0;

    @Column(name = "incidentes_medios", nullable = false, columnDefinition = "int default 0")
    private int incidentesMedios = 0;

    @Column(name = "incidentes_bajos", nullable = false, columnDefinition = "int default 0")
    private int incidentesBajos = 0;

    // Métricas de tiempo (en minutos)
    @Column(name = "tiempo_promedio_resolucion", precision = 10, scale = 2)
    private BigDecimal tiempoPromedioResolucion = BigDecimal.ZERO;

    @Column(name = "tiempo_promedio_respuesta", precision = 10, scale = 2)
    private BigDecimal tiempoPromedioRespuesta = BigDecimal.ZERO;

    @Column(name = "tiempo_total_resolucion", nullable = false, columnDefinition = "int default 0")
    private int tiempoTotalResolucion = 0;

    @Column(name = "tiempo_total_respuesta", nullable = false, columnDefinition = "int default 0")
    private int tiempoTotalRespuesta = 0;

    // Métricas de calidad
    @Column(name = "incidentes_reincidentes", nullable = false, columnDefinition = "int default 0")
    private int incidentesReincidentes = 0;

    @Column(name = "porcentaje_resolucion", precision = 5, scale = 2)
    private BigDecimal porcentajeResolucion = BigDecimal.ZERO;

    @Column(name = "satisfaccion_promedio", precision = 3, scale = 2)
    private BigDecimal satisfaccionPromedio = BigDecimal.ZERO;

    // Metadatos
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Constructors
    public EstadisticaIncidente() {
    }

    public EstadisticaIncidente(PeriodoTipo periodoTipo, int anio) {
        this.periodoTipo = periodoTipo;
        this.anio = anio;
        this.fechaCreacion = LocalDateTime.now();
    }

    public EstadisticaIncidente(PeriodoTipo periodoTipo, int anio, Tecnico tecnico) {
        this.periodoTipo = periodoTipo;
        this.anio = anio;
        this.tecnico = tecnico;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Métodos de cálculo
    public void calcularPorcentajeResolucion() {
        int totalIncidentes = this.incidentesReportados;
        if (totalIncidentes > 0) {
            this.porcentajeResolucion = BigDecimal.valueOf(this.incidentesResueltos)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalIncidentes), 2, RoundingMode.HALF_UP);
        }
    }

    public void calcularTiemposPromedios() {
        if (this.incidentesResueltos > 0) {
            this.tiempoPromedioResolucion = BigDecimal.valueOf(this.tiempoTotalResolucion)
                    .divide(BigDecimal.valueOf(this.incidentesResueltos), 2, RoundingMode.HALF_UP);
        }

        if (this.incidentesReportados > 0) {
            this.tiempoPromedioRespuesta = BigDecimal.valueOf(this.tiempoTotalRespuesta)
                    .divide(BigDecimal.valueOf(this.incidentesReportados), 2, RoundingMode.HALF_UP);
        }
    }

    public void actualizarFechaModificacion() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Métodos de incremento
    public void incrementarIncidentesReportados() {
        this.incidentesReportados++;
        actualizarFechaModificacion();
    }

    public void incrementarIncidentesResueltos(int tiempoResolucionMinutos) {
        this.incidentesResueltos++;
        this.tiempoTotalResolucion += tiempoResolucionMinutos;
        if (this.incidentesEnProgreso > 0) {
            this.incidentesEnProgreso--;
        }
        calcularPorcentajeResolucion();
        calcularTiemposPromedios();
        actualizarFechaModificacion();
    }

    public void incrementarIncidentesPorSeveridad(String severidad) {
        switch (severidad.toUpperCase()) {
            case "CRITICO":
                this.incidentesCriticos++;
                break;
            case "ALTO":
                this.incidentesAltos++;
                break;
            case "MEDIO":
                this.incidentesMedios++;
                break;
            case "BAJO":
                this.incidentesBajos++;
                break;
        }
        actualizarFechaModificacion();
    }

    public void marcarComoEnProgreso() {
        this.incidentesEnProgreso++;
        actualizarFechaModificacion();
    }

    public void incrementarEscalados() {
        this.incidentesEscalados++;
        actualizarFechaModificacion();
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

    public Tecnico getTecnico() {
        return tecnico;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }

    public Usuario getUsuarioReportador() {
        return usuarioReportador;
    }

    public void setUsuarioReportador(Usuario usuarioReportador) {
        this.usuarioReportador = usuarioReportador;
    }

    public int getIncidentesReportados() {
        return incidentesReportados;
    }

    public void setIncidentesReportados(int incidentesReportados) {
        this.incidentesReportados = incidentesReportados;
    }

    public int getIncidentesResueltos() {
        return incidentesResueltos;
    }

    public void setIncidentesResueltos(int incidentesResueltos) {
        this.incidentesResueltos = incidentesResueltos;
    }

    public int getIncidentesEnProgreso() {
        return incidentesEnProgreso;
    }

    public void setIncidentesEnProgreso(int incidentesEnProgreso) {
        this.incidentesEnProgreso = incidentesEnProgreso;
    }

    public int getIncidentesEscalados() {
        return incidentesEscalados;
    }

    public void setIncidentesEscalados(int incidentesEscalados) {
        this.incidentesEscalados = incidentesEscalados;
    }

    public int getIncidentesCriticos() {
        return incidentesCriticos;
    }

    public void setIncidentesCriticos(int incidentesCriticos) {
        this.incidentesCriticos = incidentesCriticos;
    }

    public int getIncidentesAltos() {
        return incidentesAltos;
    }

    public void setIncidentesAltos(int incidentesAltos) {
        this.incidentesAltos = incidentesAltos;
    }

    public int getIncidentesMedios() {
        return incidentesMedios;
    }

    public void setIncidentesMedios(int incidentesMedios) {
        this.incidentesMedios = incidentesMedios;
    }

    public int getIncidentesBajos() {
        return incidentesBajos;
    }

    public void setIncidentesBajos(int incidentesBajos) {
        this.incidentesBajos = incidentesBajos;
    }

    public BigDecimal getTiempoPromedioResolucion() {
        return tiempoPromedioResolucion;
    }

    public void setTiempoPromedioResolucion(BigDecimal tiempoPromedioResolucion) {
        this.tiempoPromedioResolucion = tiempoPromedioResolucion;
    }

    public BigDecimal getTiempoPromedioRespuesta() {
        return tiempoPromedioRespuesta;
    }

    public void setTiempoPromedioRespuesta(BigDecimal tiempoPromedioRespuesta) {
        this.tiempoPromedioRespuesta = tiempoPromedioRespuesta;
    }

    public int getTiempoTotalResolucion() {
        return tiempoTotalResolucion;
    }

    public void setTiempoTotalResolucion(int tiempoTotalResolucion) {
        this.tiempoTotalResolucion = tiempoTotalResolucion;
    }

    public int getTiempoTotalRespuesta() {
        return tiempoTotalRespuesta;
    }

    public void setTiempoTotalRespuesta(int tiempoTotalRespuesta) {
        this.tiempoTotalRespuesta = tiempoTotalRespuesta;
    }

    public int getIncidentesReincidentes() {
        return incidentesReincidentes;
    }

    public void setIncidentesReincidentes(int incidentesReincidentes) {
        this.incidentesReincidentes = incidentesReincidentes;
    }

    public BigDecimal getPorcentajeResolucion() {
        return porcentajeResolucion;
    }

    public void setPorcentajeResolucion(BigDecimal porcentajeResolucion) {
        this.porcentajeResolucion = porcentajeResolucion;
    }

    public BigDecimal getSatisfaccionPromedio() {
        return satisfaccionPromedio;
    }

    public void setSatisfaccionPromedio(BigDecimal satisfaccionPromedio) {
        this.satisfaccionPromedio = satisfaccionPromedio;
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
