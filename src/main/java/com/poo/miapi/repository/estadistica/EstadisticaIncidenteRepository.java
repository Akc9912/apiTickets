package com.poo.miapi.repository.estadistica;

import com.poo.miapi.model.estadistica.EstadisticaIncidente;
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
 * Repository para estadísticas de incidentes técnicos
 * Consultas especializadas por técnico, período y tipo de incidente
 */
@Repository
public interface EstadisticaIncidenteRepository extends JpaRepository<EstadisticaIncidente, Integer> {

    // Buscar estadística específica por período
    Optional<EstadisticaIncidente> findByPeriodoTipoAndAnioAndMesAndSemanaAndDiaAndTrimestre(
            PeriodoTipo periodoTipo, int anio,
            Integer mes, Integer semana, Integer dia, Integer trimestre);

    // Buscar estadística específica por técnico y período
    Optional<EstadisticaIncidente> findByTecnicoAndPeriodoTipoAndAnioAndMesAndSemanaAndDiaAndTrimestre(
            Tecnico tecnico, PeriodoTipo periodoTipo, int anio,
            Integer mes, Integer semana, Integer dia, Integer trimestre);

    // Buscar por usuario reportador
    Optional<EstadisticaIncidente> findByUsuarioReportadorAndPeriodoTipoAndAnioAndMesAndSemanaAndDiaAndTrimestre(
            Usuario usuarioReportador, PeriodoTipo periodoTipo, int anio,
            Integer mes, Integer semana, Integer dia, Integer trimestre);

    // Estadísticas generales por período
    List<EstadisticaIncidente> findByPeriodoTipoAndAnioAndMes(
            PeriodoTipo periodoTipo, int anio, Integer mes);

    // Estadísticas por técnico en un período
    List<EstadisticaIncidente> findByTecnicoAndPeriodoTipoAndAnio(
            Tecnico tecnico, PeriodoTipo periodoTipo, int anio);

    // Top técnicos por incidentes resueltos
    @Query("SELECT ei FROM EstadisticaIncidente ei WHERE ei.tecnico IS NOT NULL " +
            "AND ei.periodoTipo = :periodoTipo AND ei.anio = :anio " +
            "AND (:mes IS NULL OR ei.mes = :mes) " +
            "ORDER BY ei.incidentesResueltos DESC")
    List<EstadisticaIncidente> findTopTecnicosPorResolucion(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Técnicos con mejor tiempo de resolución
    @Query("SELECT ei FROM EstadisticaIncidente ei WHERE ei.tecnico IS NOT NULL " +
            "AND ei.periodoTipo = :periodoTipo AND ei.anio = :anio " +
            "AND (:mes IS NULL OR ei.mes = :mes) " +
            "AND ei.incidentesResueltos > 0 " +
            "ORDER BY ei.tiempoPromedioResolucion ASC")
    List<EstadisticaIncidente> findTecnicosMejorTiempo(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Estadísticas por severidad en un período
    @Query("SELECT SUM(ei.incidentesCriticos), SUM(ei.incidentesAltos), " +
            "SUM(ei.incidentesMedios), SUM(ei.incidentesBajos) " +
            "FROM EstadisticaIncidente ei WHERE ei.periodoTipo = :periodoTipo " +
            "AND ei.anio = :anio AND (:mes IS NULL OR ei.mes = :mes)")
    Object[] findDistribucionPorSeveridad(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Incidentes por técnico con más escalaciones
    @Query("SELECT ei FROM EstadisticaIncidente ei WHERE ei.tecnico IS NOT NULL " +
            "AND ei.periodoTipo = :periodoTipo AND ei.anio = :anio " +
            "AND (:mes IS NULL OR ei.mes = :mes) " +
            "ORDER BY ei.incidentesEscalados DESC")
    List<EstadisticaIncidente> findTecnicosConMasEscalaciones(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Resumen general de incidentes
    @Query("SELECT SUM(ei.incidentesReportados), SUM(ei.incidentesResueltos), " +
            "SUM(ei.incidentesEnProgreso), SUM(ei.incidentesEscalados), " +
            "AVG(ei.tiempoPromedioResolucion), AVG(ei.porcentajeResolucion) " +
            "FROM EstadisticaIncidente ei WHERE ei.periodoTipo = :periodoTipo " +
            "AND ei.anio = :anio AND (:mes IS NULL OR ei.mes = :mes)")
    Object[] findResumenGeneral(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Análisis de tendencias por mes
    @Query("SELECT ei.mes, SUM(ei.incidentesReportados), SUM(ei.incidentesResueltos), " +
            "AVG(ei.tiempoPromedioResolucion) " +
            "FROM EstadisticaIncidente ei WHERE ei.periodoTipo = 'MENSUAL' " +
            "AND ei.anio = :anio " +
            "GROUP BY ei.mes ORDER BY ei.mes")
    List<Object[]> findTendenciasPorMes(@Param("anio") int anio);

    // Técnicos con más incidentes reincidentes
    @Query("SELECT ei FROM EstadisticaIncidente ei WHERE ei.tecnico IS NOT NULL " +
            "AND ei.periodoTipo = :periodoTipo AND ei.anio = :anio " +
            "AND (:mes IS NULL OR ei.mes = :mes) " +
            "ORDER BY ei.incidentesReincidentes DESC")
    List<EstadisticaIncidente> findTecnicosConMasReincidencias(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Comparación de rendimiento entre técnicos
    @Query("SELECT ei.tecnico.id, ei.tecnico.nombre, ei.tecnico.apellido, " +
            "ei.incidentesResueltos, ei.tiempoPromedioResolucion, " +
            "ei.porcentajeResolucion, ei.incidentesEscalados " +
            "FROM EstadisticaIncidente ei WHERE ei.tecnico IS NOT NULL " +
            "AND ei.periodoTipo = :periodoTipo AND ei.anio = :anio " +
            "AND (:mes IS NULL OR ei.mes = :mes) " +
            "ORDER BY ei.porcentajeResolucion DESC, ei.tiempoPromedioResolucion ASC")
    List<Object[]> findComparacionTecnicos(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Dashboard ejecutivo de incidentes
    @Query("SELECT COUNT(DISTINCT ei.tecnico.id), " +
            "SUM(ei.incidentesReportados), SUM(ei.incidentesResueltos), " +
            "AVG(ei.tiempoPromedioResolucion), " +
            "SUM(ei.incidentesCriticos + ei.incidentesAltos) as incidentesGraves " +
            "FROM EstadisticaIncidente ei WHERE ei.periodoTipo = :periodoTipo " +
            "AND ei.anio = :anio AND (:mes IS NULL OR ei.mes = :mes)")
    Object[] findDashboardEjecutivo(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Usuarios que más incidentes reportan
    @Query("SELECT ei.usuarioReportador, SUM(ei.incidentesReportados) " +
            "FROM EstadisticaIncidente ei WHERE ei.usuarioReportador IS NOT NULL " +
            "AND ei.periodoTipo = :periodoTipo AND ei.anio = :anio " +
            "AND (:mes IS NULL OR ei.mes = :mes) " +
            "GROUP BY ei.usuarioReportador " +
            "ORDER BY SUM(ei.incidentesReportados) DESC")
    List<Object[]> findUsuariosConMasReportes(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio, @Param("mes") Integer mes);

    // Buscar por rango de fechas
    @Query("SELECT ei FROM EstadisticaIncidente ei WHERE ei.anio = :anio " +
            "AND (:mesInicio IS NULL OR ei.mes >= :mesInicio) " +
            "AND (:mesFin IS NULL OR ei.mes <= :mesFin) " +
            "AND ei.periodoTipo = :periodoTipo " +
            "ORDER BY ei.mes ASC")
    List<EstadisticaIncidente> findPorRangoFechas(
            @Param("periodoTipo") PeriodoTipo periodoTipo,
            @Param("anio") int anio,
            @Param("mesInicio") Integer mesInicio,
            @Param("mesFin") Integer mesFin);
}
