package com.poo.miapi.repository.estadistica;

import com.poo.miapi.model.estadistica.EstadisticaPeriodo;
import com.poo.miapi.model.enums.PeriodoTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EstadisticaPeriodoRepository extends JpaRepository<EstadisticaPeriodo, Integer> {

        // Buscar estadística específica por período
        Optional<EstadisticaPeriodo> findByPeriodoTipoAndAnioAndMesAndSemanaAndDiaAndTrimestre(
                        PeriodoTipo periodoTipo, int anio, Integer mes, Integer semana, Integer dia, Integer trimestre);

        // Buscar por tipo de período y año
        List<EstadisticaPeriodo> findByPeriodoTipoAndAnio(PeriodoTipo periodoTipo, int anio);

        // Buscar por tipo de período, año y mes
        List<EstadisticaPeriodo> findByPeriodoTipoAndAnioAndMes(PeriodoTipo periodoTipo, int anio, int mes);

        // Obtener estadísticas más recientes de un tipo
        @Query("SELECT e FROM EstadisticaPeriodo e WHERE e.periodoTipo = :tipo ORDER BY e.fechaCalculo DESC")
        List<EstadisticaPeriodo> findTopByPeriodoTipoOrderByFechaCalculoDesc(@Param("tipo") PeriodoTipo tipo);

        // Obtener estadísticas de un rango de fechas
        @Query("SELECT e FROM EstadisticaPeriodo e WHERE e.fechaCalculo BETWEEN :inicio AND :fin ORDER BY e.fechaCalculo DESC")
        List<EstadisticaPeriodo> findByFechaCalculoBetween(@Param("inicio") LocalDateTime inicio,
                        @Param("fin") LocalDateTime fin);

        // Estadísticas mensuales del año actual
        @Query("SELECT e FROM EstadisticaPeriodo e WHERE e.periodoTipo = 'MENSUAL' AND e.anio = :anio ORDER BY e.mes")
        List<EstadisticaPeriodo> findEstadisticasMensualesDelAnio(@Param("anio") int anio);

        // Comparar períodos (útil para tendencias)
        @Query("""
                        SELECT e FROM EstadisticaPeriodo e
                        WHERE e.periodoTipo = :tipo
                        AND ((e.anio = :anio1 AND e.mes = :mes1) OR (e.anio = :anio2 AND e.mes = :mes2))
                        ORDER BY e.fechaCalculo
                        """)
        List<EstadisticaPeriodo> compararPeriodos(
                        @Param("tipo") PeriodoTipo tipo,
                        @Param("anio1") int anio1, @Param("mes1") int mes1,
                        @Param("anio2") int anio2, @Param("mes2") int mes2);

        // Top N períodos por tickets creados
        @Query("SELECT e FROM EstadisticaPeriodo e WHERE e.periodoTipo = :tipo ORDER BY e.ticketsCreados DESC")
        List<EstadisticaPeriodo> findTopByTicketsCreados(@Param("tipo") PeriodoTipo tipo);

        // Períodos con mejor calidad (% aprobación)
        @Query("SELECT e FROM EstadisticaPeriodo e WHERE e.periodoTipo = :tipo ORDER BY e.porcentajeAprobacion DESC")
        List<EstadisticaPeriodo> findTopByCalidad(@Param("tipo") PeriodoTipo tipo);

        // Estadísticas con tickets pendientes
        @Query("SELECT e FROM EstadisticaPeriodo e WHERE e.ticketsPendientes > 0 ORDER BY e.ticketsPendientes DESC")
        List<EstadisticaPeriodo> findConTicketsPendientes();

        // Verificar si existe estadística para un período específico
        @Query("""
                        SELECT COUNT(e) > 0 FROM EstadisticaPeriodo e
                        WHERE e.periodoTipo = :tipo
                        AND e.anio = :anio
                        AND (:mes IS NULL OR e.mes = :mes)
                        AND (:dia IS NULL OR e.dia = :dia)
                        """)
        boolean existeEstadisticaPorPeriodo(
                        @Param("tipo") PeriodoTipo tipo,
                        @Param("anio") int anio,
                        @Param("mes") Integer mes,
                        @Param("dia") Integer dia);

        // Obtener resumen rápido para dashboard
        @Query("""
                        SELECT
                            SUM(e.ticketsCreados) as totalCreados,
                            SUM(e.ticketsResueltos) as totalResueltos,
                            SUM(e.ticketsFinalizados) as totalFinalizados,
                            AVG(e.porcentajeAprobacion) as promedioCalidad
                        FROM EstadisticaPeriodo e
                        WHERE e.periodoTipo = :tipo
                        AND e.anio = :anio
                        """)
        Object[] getResumenAnual(@Param("tipo") PeriodoTipo tipo, @Param("anio") int anio);

        // Estadísticas desactualizadas (más de X horas)
        @Query("SELECT e FROM EstadisticaPeriodo e WHERE e.ultimaActualizacion < :fecha")
        List<EstadisticaPeriodo> findDesactualizadas(@Param("fecha") LocalDateTime fecha);

        // Obtener estadísticas ordenadas por fecha para tendencias
        @Query("SELECT e FROM EstadisticaPeriodo e WHERE e.periodoTipo = :tipo ORDER BY e.anio DESC, e.mes DESC")
        List<EstadisticaPeriodo> findByPeriodoTipoOrderByAnioDescMesDesc(@Param("tipo") PeriodoTipo tipo);
}
