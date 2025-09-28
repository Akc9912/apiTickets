package com.poo.miapi.model.estadistica;

import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.model.enums.PeriodoTipo;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Estadísticas específicas de solicitudes de devolución
 * Métricas de aprobación, tiempos de procesamiento, patrones
 */
@Entity
@Table(name = "estadistica_devolucion", indexes = {
        @Index(name = "idx_devolucion_periodo", columnList = "periodo_tipo, anio, mes"),
        @Index(name = "idx_devolucion_tecnico", columnList = "tecnico_id, anio, mes"),
        @Index(name = "idx_devolucion_evaluador", columnList = "usuario_evaluador_id, anio, mes")
})
public class EstadisticaDevolucion {

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
    @JoinColumn(name = "usuario_evaluador_id")
    private Usuario usuarioEvaluador;

    // Métricas básicas de devoluciones
    @Column(name = "solicitudes_creadas", nullable = false, columnDefinition = "int default 0")
    private int solicitudesCreadas = 0;

    @Column(name = "solicitudes_aprobadas", nullable = false, columnDefinition = "int default 0")
    private int solicitudesAprobadas = 0;

    @Column(name = "solicitudes_rechazadas", nullable = false, columnDefinition = "int default 0")
    private int solicitudesRechazadas = 0;

    @Column(name = "solicitudes_pendientes", nullable = false, columnDefinition = "int default 0")
    private int solicitudesPendientes = 0;

    @Column(name = "solicitudes_canceladas", nullable = false, columnDefinition = "int default 0")
    private int solicitudesCanceladas = 0;

    // Métricas por motivo de devolución
    @Column(name = "motivo_incompleto", nullable = false, columnDefinition = "int default 0")
    private int motivoIncompleto = 0;

    @Column(name = "motivo_error_tecnico", nullable = false, columnDefinition = "int default 0")
    private int motivoErrorTecnico = 0;

    @Column(name = "motivo_informacion_adicional", nullable = false, columnDefinition = "int default 0")
    private int motivoInformacionAdicional = 0;

    @Column(name = "motivo_cambio_alcance", nullable = false, columnDefinition = "int default 0")
    private int motivoCambioAlcance = 0;

    @Column(name = "motivo_otros", nullable = false, columnDefinition = "int default 0")
    private int motivoOtros = 0;

    // Métricas de tiempo (en minutos)
    @Column(name = "tiempo_promedio_procesamiento", precision = 10, scale = 2)
    private BigDecimal tiempoPromedioProcesamiento = BigDecimal.ZERO;

    @Column(name = "tiempo_total_procesamiento", nullable = false, columnDefinition = "int default 0")
    private int tiempoTotalProcesamiento = 0;

    @Column(name = "tiempo_promedio_respuesta", precision = 10, scale = 2)
    private BigDecimal tiempoPromedioRespuesta = BigDecimal.ZERO;

    @Column(name = "tiempo_total_respuesta", nullable = false, columnDefinition = "int default 0")
    private int tiempoTotalRespuesta = 0;

    // Métricas de calidad y patterns
    @Column(name = "devoluciones_recurrentes", nullable = false, columnDefinition = "int default 0")
    private int devolucionesRecurrentes = 0;

    @Column(name = "porcentaje_aprobacion", precision = 5, scale = 2)
    private BigDecimal porcentajeAprobacion = BigDecimal.ZERO;

    @Column(name = "porcentaje_recurrencia", precision = 5, scale = 2)
    private BigDecimal porcentajeRecurrencia = BigDecimal.ZERO;

    @Column(name = "impacto_productividad", precision = 5, scale = 2)
    private BigDecimal impactoProductividad = BigDecimal.ZERO;

    // Métricas de impacto
    @Column(name = "tickets_afectados", nullable = false, columnDefinition = "int default 0")
    private int ticketsAfectados = 0;

    @Column(name = "horas_trabajo_perdidas", precision = 10, scale = 2)
    private BigDecimal horasTrabajoPeridas = BigDecimal.ZERO;

    // Metadatos
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Constructors
    public EstadisticaDevolucion() {
    }

    public EstadisticaDevolucion(PeriodoTipo periodoTipo, int anio) {
        this.periodoTipo = periodoTipo;
        this.anio = anio;
        this.fechaCreacion = LocalDateTime.now();
    }

    public EstadisticaDevolucion(PeriodoTipo periodoTipo, int anio, Tecnico tecnico) {
        this.periodoTipo = periodoTipo;
        this.anio = anio;
        this.tecnico = tecnico;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Métodos de cálculo
    public void calcularPorcentajeAprobacion() {
        int totalProcesadas = this.solicitudesAprobadas + this.solicitudesRechazadas;
        if (totalProcesadas > 0) {
            this.porcentajeAprobacion = BigDecimal.valueOf(this.solicitudesAprobadas)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalProcesadas), 2, RoundingMode.HALF_UP);
        }
    }

    public void calcularPorcentajeRecurrencia() {
        if (this.solicitudesCreadas > 0) {
            this.porcentajeRecurrencia = BigDecimal.valueOf(this.devolucionesRecurrentes)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(this.solicitudesCreadas), 2, RoundingMode.HALF_UP);
        }
    }

    public void calcularTiemposPromedios() {
        int totalProcesadas = this.solicitudesAprobadas + this.solicitudesRechazadas;
        if (totalProcesadas > 0) {
            this.tiempoPromedioProcesamiento = BigDecimal.valueOf(this.tiempoTotalProcesamiento)
                    .divide(BigDecimal.valueOf(totalProcesadas), 2, RoundingMode.HALF_UP);
        }

        if (this.solicitudesCreadas > 0) {
            this.tiempoPromedioRespuesta = BigDecimal.valueOf(this.tiempoTotalRespuesta)
                    .divide(BigDecimal.valueOf(this.solicitudesCreadas), 2, RoundingMode.HALF_UP);
        }
    }

    public void actualizarFechaModificacion() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Métodos de incremento
    public void incrementarSolicitudesCreadas() {
        this.solicitudesCreadas++;
        this.solicitudesPendientes++;
        actualizarFechaModificacion();
    }

    public void procesarSolicitudAprobada(int tiempoProcesamiento) {
        this.solicitudesAprobadas++;
        this.tiempoTotalProcesamiento += tiempoProcesamiento;
        if (this.solicitudesPendientes > 0) {
            this.solicitudesPendientes--;
        }
        calcularPorcentajeAprobacion();
        calcularTiemposPromedios();
        actualizarFechaModificacion();
    }

    public void procesarSolicitudRechazada(int tiempoProcesamiento) {
        this.solicitudesRechazadas++;
        this.tiempoTotalProcesamiento += tiempoProcesamiento;
        if (this.solicitudesPendientes > 0) {
            this.solicitudesPendientes--;
        }
        calcularPorcentajeAprobacion();
        calcularTiemposPromedios();
        actualizarFechaModificacion();
    }

    public void incrementarMotivo(String motivo) {
        switch (motivo.toUpperCase()) {
            case "INCOMPLETO":
                this.motivoIncompleto++;
                break;
            case "ERROR_TECNICO":
                this.motivoErrorTecnico++;
                break;
            case "INFORMACION_ADICIONAL":
                this.motivoInformacionAdicional++;
                break;
            case "CAMBIO_ALCANCE":
                this.motivoCambioAlcance++;
                break;
            default:
                this.motivoOtros++;
                break;
        }
        actualizarFechaModificacion();
    }

    public void marcarComoRecurrente() {
        this.devolucionesRecurrentes++;
        calcularPorcentajeRecurrencia();
        actualizarFechaModificacion();
    }

    public void registrarImpacto(int ticketsAfectados, BigDecimal horasPerdidas) {
        this.ticketsAfectados += ticketsAfectados;
        this.horasTrabajoPeridas = this.horasTrabajoPeridas.add(horasPerdidas);
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

    public Usuario getUsuarioEvaluador() {
        return usuarioEvaluador;
    }

    public void setUsuarioEvaluador(Usuario usuarioEvaluador) {
        this.usuarioEvaluador = usuarioEvaluador;
    }

    public int getSolicitudesCreadas() {
        return solicitudesCreadas;
    }

    public void setSolicitudesCreadas(int solicitudesCreadas) {
        this.solicitudesCreadas = solicitudesCreadas;
    }

    public int getSolicitudesAprobadas() {
        return solicitudesAprobadas;
    }

    public void setSolicitudesAprobadas(int solicitudesAprobadas) {
        this.solicitudesAprobadas = solicitudesAprobadas;
    }

    public int getSolicitudesRechazadas() {
        return solicitudesRechazadas;
    }

    public void setSolicitudesRechazadas(int solicitudesRechazadas) {
        this.solicitudesRechazadas = solicitudesRechazadas;
    }

    public int getSolicitudesPendientes() {
        return solicitudesPendientes;
    }

    public void setSolicitudesPendientes(int solicitudesPendientes) {
        this.solicitudesPendientes = solicitudesPendientes;
    }

    public int getSolicitudesCanceladas() {
        return solicitudesCanceladas;
    }

    public void setSolicitudesCanceladas(int solicitudesCanceladas) {
        this.solicitudesCanceladas = solicitudesCanceladas;
    }

    public int getMotivoIncompleto() {
        return motivoIncompleto;
    }

    public void setMotivoIncompleto(int motivoIncompleto) {
        this.motivoIncompleto = motivoIncompleto;
    }

    public int getMotivoErrorTecnico() {
        return motivoErrorTecnico;
    }

    public void setMotivoErrorTecnico(int motivoErrorTecnico) {
        this.motivoErrorTecnico = motivoErrorTecnico;
    }

    public int getMotivoInformacionAdicional() {
        return motivoInformacionAdicional;
    }

    public void setMotivoInformacionAdicional(int motivoInformacionAdicional) {
        this.motivoInformacionAdicional = motivoInformacionAdicional;
    }

    public int getMotivoCambioAlcance() {
        return motivoCambioAlcance;
    }

    public void setMotivoCambioAlcance(int motivoCambioAlcance) {
        this.motivoCambioAlcance = motivoCambioAlcance;
    }

    public int getMotivoOtros() {
        return motivoOtros;
    }

    public void setMotivoOtros(int motivoOtros) {
        this.motivoOtros = motivoOtros;
    }

    public BigDecimal getTiempoPromedioProcesamiento() {
        return tiempoPromedioProcesamiento;
    }

    public void setTiempoPromedioProcesamiento(BigDecimal tiempoPromedioProcesamiento) {
        this.tiempoPromedioProcesamiento = tiempoPromedioProcesamiento;
    }

    public int getTiempoTotalProcesamiento() {
        return tiempoTotalProcesamiento;
    }

    public void setTiempoTotalProcesamiento(int tiempoTotalProcesamiento) {
        this.tiempoTotalProcesamiento = tiempoTotalProcesamiento;
    }

    public BigDecimal getTiempoPromedioRespuesta() {
        return tiempoPromedioRespuesta;
    }

    public void setTiempoPromedioRespuesta(BigDecimal tiempoPromedioRespuesta) {
        this.tiempoPromedioRespuesta = tiempoPromedioRespuesta;
    }

    public int getTiempoTotalRespuesta() {
        return tiempoTotalRespuesta;
    }

    public void setTiempoTotalRespuesta(int tiempoTotalRespuesta) {
        this.tiempoTotalRespuesta = tiempoTotalRespuesta;
    }

    public int getDevolucionesRecurrentes() {
        return devolucionesRecurrentes;
    }

    public void setDevolucionesRecurrentes(int devolucionesRecurrentes) {
        this.devolucionesRecurrentes = devolucionesRecurrentes;
    }

    public BigDecimal getPorcentajeAprobacion() {
        return porcentajeAprobacion;
    }

    public void setPorcentajeAprobacion(BigDecimal porcentajeAprobacion) {
        this.porcentajeAprobacion = porcentajeAprobacion;
    }

    public BigDecimal getPorcentajeRecurrencia() {
        return porcentajeRecurrencia;
    }

    public void setPorcentajeRecurrencia(BigDecimal porcentajeRecurrencia) {
        this.porcentajeRecurrencia = porcentajeRecurrencia;
    }

    public BigDecimal getImpactoProductividad() {
        return impactoProductividad;
    }

    public void setImpactoProductividad(BigDecimal impactoProductividad) {
        this.impactoProductividad = impactoProductividad;
    }

    public int getTicketsAfectados() {
        return ticketsAfectados;
    }

    public void setTicketsAfectados(int ticketsAfectados) {
        this.ticketsAfectados = ticketsAfectados;
    }

    public BigDecimal getHorasTrabajoPeridas() {
        return horasTrabajoPeridas;
    }

    public void setHorasTrabajoPeridas(BigDecimal horasTrabajoPeridas) {
        this.horasTrabajoPeridas = horasTrabajoPeridas;
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
