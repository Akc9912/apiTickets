package com.poo.miapi.repository.estadistica;

import com.poo.miapi.model.estadistica.EstadisticaDevolucion;
import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.model.enums.PeriodoTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para estadísticas de solicitudes de devolución
 * Consultas especializadas por técnico, evaluador y patrones de devolución
 */
@Repository
public interface EstadisticaDevolucionRepository extends JpaRepository<EstadisticaDevolucion, Integer> {

    // Buscar estadística específica por período
    Optional<EstadisticaDevolucion> findByPeriodoTipoAndAnioAndMesAndSemanaAndDiaAndTrimestre(
            PeriodoTipo periodoTipo, int anio,
            Integer mes, Integer semana, Integer dia, Integer trimestre);

    // Buscar estadística específica por técnico y período
    Optional<EstadisticaDevolucion> findByTecnicoAndPeriodoTipoAndAnioAndMesAndSemanaAndDiaAndTrimestre(
            Tecnico tecnico, PeriodoTipo periodoTipo, int anio,
            Integer mes, Integer semana, Integer dia, Integer trimestre);

    // Buscar por evaluador
    Optional<EstadisticaDevolucion> findByUsuarioEvaluadorAndPeriodoTipoAndAnioAndMesAndSemanaAndDiaAndTrimestre(
            Usuario usuarioEvaluador, PeriodoTipo periodoTipo, int anio,
            Integer mes, Integer semana, Integer dia, Integer trimestre);

    // Estadísticas generales por período
    List<EstadisticaDevolucion> findByPeriodoTipoAndAnioAndMes(
            PeriodoTipo periodoTipo, int anio, Integer mes);

    // Estadísticas por técnico en un período
    List<EstadisticaDevolucion> findByTecnicoAndPeriodoTipoAndAnio(
            Tecnico tecnico, PeriodoTipo periodoTipo, int anio);

    // Técnicos con más solicitudes de devolución
    @Query("SELECT ed FROM EstadisticaDevolucion ed WHERE ed.tecnico IS NOT NULL " +
            "AND ed.periodoTipo = :periodoTipo AND ed.anio = :anio " +
            "AND (:mes IS NULL OR ed.mes = :mes) " +
            "ORDER BY ed.solicitudesCreadas DESC")
    List<EstadisticaDevolucion> findTecnicosConMasSolicitudes(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Técnicos con peor tasa de aprobación
    @Query("SELECT ed FROM EstadisticaDevolucion ed WHERE ed.tecnico IS NOT NULL " +
            "AND ed.periodoTipo = :periodoTipo AND ed.anio = :anio " +
            "AND (:mes IS NULL OR ed.mes = :mes) " +
            "AND ed.solicitudesCreadas > 0 " +
            "ORDER BY ed.porcentajeAprobacion DESC")
    List<EstadisticaDevolucion> findTecnicosConMejorTasaAprobacion(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Análisis por motivos de devolución
    @Query("SELECT SUM(ed.motivoIncompleto), SUM(ed.motivoErrorTecnico), " +
            "SUM(ed.motivoInformacionAdicional), SUM(ed.motivoCambioAlcance), " +
            "SUM(ed.motivoOtros) " +
            "FROM EstadisticaDevolucion ed WHERE ed.periodoTipo = :periodoTipo " +
            "AND ed.anio = :anio AND (:mes IS NULL OR ed.mes = :mes)")
    Object[] findDistribucionPorMotivos(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Evaluadores más eficientes (menor tiempo de procesamiento)
    @Query("SELECT ed FROM EstadisticaDevolucion ed WHERE ed.usuarioEvaluador IS NOT NULL " +
            "AND ed.periodoTipo = :periodoTipo AND ed.anio = :anio " +
            "AND (:mes IS NULL OR ed.mes = :mes) " +
            "AND ed.solicitudesAprobadas + ed.solicitudesRechazadas > 0 " +
            "ORDER BY ed.tiempoPromedioProcesamiento ASC")
    List<EstadisticaDevolucion> findEvaluadoresMasEficientes(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Resumen general de devoluciones
    @Query("SELECT SUM(ed.solicitudesCreadas), SUM(ed.solicitudesAprobadas), " +
            "SUM(ed.solicitudesRechazadas), SUM(ed.solicitudesPendientes), " +
            "AVG(ed.porcentajeAprobacion), AVG(ed.tiempoPromedioProcesamiento) " +
            "FROM EstadisticaDevolucion ed WHERE ed.periodoTipo = :periodoTipo " +
            "AND ed.anio = :anio AND (:mes IS NULL OR ed.mes = :mes)")
    Object[] findResumenGeneral(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Tendencias mensuales
    @Query("SELECT ed.mes, SUM(ed.solicitudesCreadas), SUM(ed.solicitudesAprobadas), " +
            "SUM(ed.solicitudesRechazadas), AVG(ed.porcentajeAprobacion) " +
            "FROM EstadisticaDevolucion ed WHERE ed.periodoTipo = 'MENSUAL' " +
            "AND ed.anio = :anio " +
            "GROUP BY ed.mes ORDER BY ed.mes")
    List<Object[]> findTendenciasMensuales(@Param("anio") int anio);

    // Técnicos con más devoluciones recurrentes
    @Query("SELECT ed FROM EstadisticaDevolucion ed WHERE ed.tecnico IS NOT NULL " +
            "AND ed.periodoTipo = :periodoTipo AND ed.anio = :anio " +
            "AND (:mes IS NULL OR ed.mes = :mes) " +
            "ORDER BY ed.devolucionesRecurrentes DESC")
    List<EstadisticaDevolucion> findTecnicosConMasRecurrentes(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Dashboard ejecutivo de devoluciones
    @Query("SELECT COUNT(DISTINCT ed.tecnico.id), " +
            "SUM(ed.solicitudesCreadas), SUM(ed.solicitudesPendientes), " +
            "AVG(ed.porcentajeAprobacion), " +
            "SUM(ed.ticketsAfectados), AVG(ed.horasTrabajoPeridas) " +
            "FROM EstadisticaDevolucion ed WHERE ed.periodoTipo = :periodoTipo " +
            "AND ed.anio = :anio AND (:mes IS NULL OR ed.mes = :mes)")
    Object[] findDashboardEjecutivo(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Impacto en productividad
    @Query("SELECT ed.tecnico.nombre, ed.tecnico.apellido, " +
            "ed.solicitudesCreadas, ed.horasTrabajoPeridas, ed.ticketsAfectados " +
            "FROM EstadisticaDevolucion ed WHERE ed.tecnico IS NOT NULL " +
            "AND ed.periodoTipo = :periodoTipo AND ed.anio = :anio " +
            "AND (:mes IS NULL OR ed.mes = :mes) " +
            "ORDER BY ed.horasTrabajoPeridas DESC")
    List<Object[]> findImpactoProductividad(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Análisis de patrones por técnico
    @Query("SELECT ed.tecnico.id, ed.tecnico.nombre, ed.tecnico.apellido, " +
            "ed.solicitudesCreadas, ed.porcentajeAprobacion, " +
            "ed.porcentajeRecurrencia, ed.motivoIncompleto, " +
            "ed.motivoErrorTecnico, ed.motivoInformacionAdicional " +
            "FROM EstadisticaDevolucion ed WHERE ed.tecnico IS NOT NULL " +
            "AND ed.periodoTipo = :periodoTipo AND ed.anio = :anio " +
            "AND (:mes IS NULL OR ed.mes = :mes) " +
            "ORDER BY ed.solicitudesCreadas DESC")
    List<Object[]> findPatronesPorTecnico(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Comparación entre evaluadores
    @Query("SELECT ed.usuarioEvaluador.nombre, ed.usuarioEvaluador.apellido, " +
            "COUNT(ed.solicitudesAprobadas + ed.solicitudesRechazadas) as totalProcesadas, " +
            "AVG(ed.porcentajeAprobacion), AVG(ed.tiempoPromedioProcesamiento) " +
            "FROM EstadisticaDevolucion ed WHERE ed.usuarioEvaluador IS NOT NULL " +
            "AND ed.periodoTipo = :periodoTipo AND ed.anio = :anio " +
            "AND (:mes IS NULL OR ed.mes = :mes) " +
            "GROUP BY ed.usuarioEvaluador " +
            "ORDER BY totalProcesadas DESC")
    List<Object[]> findComparacionEvaluadores(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Buscar por rango de fechas
    @Query("SELECT ed FROM EstadisticaDevolucion ed WHERE ed.anio = :anio " +
            "AND (:mesInicio IS NULL OR ed.mes >= :mesInicio) " +
            "AND (:mesFin IS NULL OR ed.mes <= :mesFin) " +
            "AND ed.periodoTipo = :periodoTipo " +
            "ORDER BY ed.mes ASC")
    List<EstadisticaDevolucion> findPorRangoFechas(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio,
            @Param("mesInicio") Integer mesInicio,
            @Param("mesFin") Integer mesFin);
}
