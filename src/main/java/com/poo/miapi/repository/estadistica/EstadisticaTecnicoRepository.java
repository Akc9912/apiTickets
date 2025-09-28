package com.poo.miapi.repository.estadistica;

import com.poo.miapi.model.estadistica.EstadisticaTecnico;
import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.enums.PeriodoTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadisticaTecnicoRepository extends JpaRepository<EstadisticaTecnico, Integer> {

    // Buscar estadística específica por técnico y período
    Optional<EstadisticaTecnico> findByTecnicoAndPeriodoTipoAndAnioAndMesAndSemanaAndDiaAndTrimestre(
            Tecnico tecnico, PeriodoTipo periodoTipo, int anio, Integer mes, Integer semana, Integer dia,
            Integer trimestre);

    // Estadísticas de un técnico por tipo de período
    List<EstadisticaTecnico> findByTecnicoAndPeriodoTipo(Tecnico tecnico, PeriodoTipo periodoTipo);

    // Estadísticas de todos los técnicos para un período específico
    List<EstadisticaTecnico> findByPeriodoTipoAndAnioAndMes(PeriodoTipo periodoTipo, int anio, Integer mes);

    // Top técnicos por productividad (tickets completados)
    @Query("SELECT e FROM EstadisticaTecnico e WHERE e.periodoTipo = :tipo AND e.anio = :anio AND e.mes = :mes ORDER BY e.ticketsCompletados DESC")
    List<EstadisticaTecnico> findTopProductividad(@Param("tipo") PeriodoTipo tipo, @Param("anio") int anio,
            @Param("mes") Integer mes);

    // Top técnicos por calidad (% aprobación)
    @Query("SELECT e FROM EstadisticaTecnico e WHERE e.periodoTipo = :tipo AND e.anio = :anio AND e.mes = :mes ORDER BY e.porcentajeAprobacion DESC")
    List<EstadisticaTecnico> findTopCalidad(@Param("tipo") PeriodoTipo tipo, @Param("anio") int anio,
            @Param("mes") Integer mes);

    // Técnicos con problemas (muchas marcas/fallas)
    @Query("SELECT e FROM EstadisticaTecnico e WHERE (e.marcasRecibidas + e.fallasRegistradas) >= :umbral ORDER BY (e.marcasRecibidas + e.fallasRegistradas) DESC")
    List<EstadisticaTecnico> findTecnicosConProblemas(@Param("umbral") int umbral);

    // Ranking actual de técnicos por productividad
    @Query("SELECT e FROM EstadisticaTecnico e WHERE e.periodoTipo = 'MENSUAL' AND e.anio = :anio AND e.mes = :mes AND e.rankingProductividad IS NOT NULL ORDER BY e.rankingProductividad")
    List<EstadisticaTecnico> getRankingProductividad(@Param("anio") int anio, @Param("mes") int mes);

    // Ranking actual de técnicos por calidad
    @Query("SELECT e FROM EstadisticaTecnico e WHERE e.periodoTipo = 'MENSUAL' AND e.anio = :anio AND e.mes = :mes AND e.rankingCalidad IS NOT NULL ORDER BY e.rankingCalidad")
    List<EstadisticaTecnico> getRankingCalidad(@Param("anio") int anio, @Param("mes") int mes);

    // Técnicos que mejoraron su rendimiento
    @Query("""
            SELECT e1 FROM EstadisticaTecnico e1
            WHERE EXISTS (
                SELECT e2 FROM EstadisticaTecnico e2
                WHERE e2.tecnico = e1.tecnico
                AND e2.anio = :anioAnterior AND e2.mes = :mesAnterior
                AND e1.anio = :anio AND e1.mes = :mes
                AND e1.ticketsCompletados > e2.ticketsCompletados
            )
            """)
    List<EstadisticaTecnico> findTecnicosMejorados(
            @Param("anio") int anio, @Param("mes") int mes,
            @Param("anioAnterior") int anioAnterior, @Param("mesAnterior") int mesAnterior);

    // Estadísticas de un técnico para comparar períodos
    @Query("SELECT e FROM EstadisticaTecnico e WHERE e.tecnico.id = :tecnicoId AND e.periodoTipo = :tipo ORDER BY e.anio DESC, e.mes DESC")
    List<EstadisticaTecnico> getHistorialTecnico(@Param("tecnicoId") int tecnicoId, @Param("tipo") PeriodoTipo tipo);

    // Promedio de tickets completados por técnico (para benchmarking)
    @Query("SELECT AVG(e.ticketsCompletados) FROM EstadisticaTecnico e WHERE e.periodoTipo = :tipo AND e.anio = :anio AND e.mes = :mes")
    Double getPromedioTicketsCompletados(@Param("tipo") PeriodoTipo tipo, @Param("anio") int anio,
            @Param("mes") Integer mes);

    // Técnicos por encima/debajo del promedio
    @Query("""
            SELECT e FROM EstadisticaTecnico e
            WHERE e.periodoTipo = :tipo AND e.anio = :anio AND e.mes = :mes
            AND e.ticketsCompletados > (
                SELECT AVG(e2.ticketsCompletados)
                FROM EstadisticaTecnico e2
                WHERE e2.periodoTipo = :tipo AND e2.anio = :anio AND e2.mes = :mes
            )
            """)
    List<EstadisticaTecnico> findTecnicosSobrePromedio(@Param("tipo") PeriodoTipo tipo, @Param("anio") int anio,
            @Param("mes") Integer mes);

    // Contar técnicos activos en un período
    @Query("SELECT COUNT(DISTINCT e.tecnico) FROM EstadisticaTecnico e WHERE e.periodoTipo = :tipo AND e.anio = :anio AND e.mes = :mes")
    long countTecnicosActivos(@Param("tipo") PeriodoTipo tipo, @Param("anio") int anio, @Param("mes") Integer mes);

    // Técnicos con más devoluciones
    @Query("SELECT e FROM EstadisticaTecnico e WHERE e.solicitudesDevolucion > 0 ORDER BY e.solicitudesDevolucion DESC")
    List<EstadisticaTecnico> findTecnicosConDevoluciones();
}
