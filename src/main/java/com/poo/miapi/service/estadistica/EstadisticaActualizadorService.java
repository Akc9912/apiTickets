package com.poo.miapi.service.estadistica;

import com.poo.miapi.model.estadistica.EstadisticaPeriodo;
import com.poo.miapi.model.estadistica.EstadisticaTecnico;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.enums.PeriodoTipo;
import com.poo.miapi.repository.estadistica.EstadisticaPeriodoRepository;
import com.poo.miapi.repository.estadistica.EstadisticaTecnicoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Servicio especializado en actualizar estadísticas incrementalmente
 * Se ejecuta en tiempo real cuando ocurren eventos
 */
@Service
@Transactional
public class EstadisticaActualizadorService {

    private static final Logger logger = LoggerFactory.getLogger(EstadisticaActualizadorService.class);

    private final EstadisticaPeriodoRepository estadisticaPeriodoRepository;
    private final EstadisticaTecnicoRepository estadisticaTecnicoRepository;

    public EstadisticaActualizadorService(
            EstadisticaPeriodoRepository estadisticaPeriodoRepository,
            EstadisticaTecnicoRepository estadisticaTecnicoRepository) {
        this.estadisticaPeriodoRepository = estadisticaPeriodoRepository;
        this.estadisticaTecnicoRepository = estadisticaTecnicoRepository;
    }

    /**
     * Incrementar tickets resueltos (cuando un técnico resuelve un ticket)
     */
    public void incrementarTicketsResueltos(Ticket ticket) {
        try {
            LocalDateTime ahora = LocalDateTime.now();

            // Actualizar estadística diaria
            EstadisticaPeriodo estadisticaDiaria = obtenerOCrearEstadisticaDiaria(ahora);
            estadisticaDiaria.incrementarTicketsResueltos();
            estadisticaPeriodoRepository.save(estadisticaDiaria);

            // Actualizar estadística mensual
            EstadisticaPeriodo estadisticaMensual = obtenerOCrearEstadisticaMensual(ahora);
            estadisticaMensual.incrementarTicketsResueltos();
            estadisticaPeriodoRepository.save(estadisticaMensual);

            logger.debug("Tickets resueltos incrementados para ticket: {}", ticket.getId());

        } catch (Exception e) {
            logger.error("Error incrementando tickets resueltos: {}", e.getMessage(), e);
        }
    }

    /**
     * Incrementar tickets finalizados (cuando un trabajador aprueba)
     */
    public void incrementarTicketsFinalizados(Ticket ticket) {
        try {
            LocalDateTime ahora = LocalDateTime.now();

            // Actualizar estadística diaria
            EstadisticaPeriodo estadisticaDiaria = obtenerOCrearEstadisticaDiaria(ahora);
            estadisticaDiaria.incrementarTicketsFinalizados();
            estadisticaPeriodoRepository.save(estadisticaDiaria);

            // Actualizar estadística mensual
            EstadisticaPeriodo estadisticaMensual = obtenerOCrearEstadisticaMensual(ahora);
            estadisticaMensual.incrementarTicketsFinalizados();
            estadisticaPeriodoRepository.save(estadisticaMensual);

            logger.debug("Tickets finalizados incrementados para ticket: {}", ticket.getId());

        } catch (Exception e) {
            logger.error("Error incrementando tickets finalizados: {}", e.getMessage(), e);
        }
    }

    /**
     * Actualizar métricas de calidad (aprobación/rechazo)
     */
    public void actualizarMetricasCalidad(Ticket ticket, Tecnico tecnico, boolean aprobado) {
        try {
            LocalDateTime ahora = LocalDateTime.now();

            // Actualizar estadística general
            EstadisticaPeriodo estadistica = obtenerOCrearEstadisticaDiaria(ahora);
            if (aprobado) {
                estadistica.setTicketsAprobados(estadistica.getTicketsAprobados() + 1);
                incrementarTicketsFinalizados(ticket);
            } else {
                estadistica.setTicketsRechazados(estadistica.getTicketsRechazados() + 1);
            }
            estadistica.calcularPorcentajeAprobacion();
            estadisticaPeriodoRepository.save(estadistica);

            // Actualizar estadística del técnico
            EstadisticaTecnico estadisticaTecnico = obtenerOCrearEstadisticaTecnico(tecnico, ahora);
            if (aprobado) {
                estadisticaTecnico.setTicketsAprobados(estadisticaTecnico.getTicketsAprobados() + 1);
                estadisticaTecnico.setTicketsCompletados(estadisticaTecnico.getTicketsCompletados() + 1);
            } else {
                estadisticaTecnico.setTicketsRechazados(estadisticaTecnico.getTicketsRechazados() + 1);
            }
            estadisticaTecnico.calcularPorcentajeAprobacion();
            estadisticaTecnicoRepository.save(estadisticaTecnico);

            logger.debug("Métricas de calidad actualizadas - Ticket: {}, Técnico: {}, Aprobado: {}",
                    ticket.getId(), tecnico.getId(), aprobado);

        } catch (Exception e) {
            logger.error("Error actualizando métricas de calidad: {}", e.getMessage(), e);
        }
    }

    /**
     * Actualizar estadísticas del técnico
     */
    public void actualizarEstadisticasTecnico(Tecnico tecnico, Ticket ticket) {
        try {
            LocalDateTime ahora = LocalDateTime.now();
            EstadisticaTecnico estadistica = obtenerOCrearEstadisticaTecnico(tecnico, ahora);

            // Incrementar tickets asignados si es necesario
            estadistica.setTicketsAsignados(estadistica.getTicketsAsignados() + 1);

            estadisticaTecnicoRepository.save(estadistica);

            logger.debug("Estadísticas de técnico actualizadas - Técnico: {}, Ticket: {}",
                    tecnico.getId(), ticket.getId());

        } catch (Exception e) {
            logger.error("Error actualizando estadísticas de técnico: {}", e.getMessage(), e);
        }
    }

    /**
     * Incrementar solicitudes de devolución
     */
    public void incrementarSolicitudesDevolucion(Ticket ticket, Tecnico tecnico) {
        try {
            LocalDateTime ahora = LocalDateTime.now();

            // Estadística general
            EstadisticaPeriodo estadistica = obtenerOCrearEstadisticaDiaria(ahora);
            estadistica.setSolicitudesDevolucion(estadistica.getSolicitudesDevolucion() + 1);
            estadisticaPeriodoRepository.save(estadistica);

            // Estadística del técnico
            EstadisticaTecnico estadisticaTecnico = obtenerOCrearEstadisticaTecnico(tecnico, ahora);
            estadisticaTecnico.setSolicitudesDevolucion(estadisticaTecnico.getSolicitudesDevolucion() + 1);
            estadisticaTecnicoRepository.save(estadisticaTecnico);

            logger.debug("Solicitudes de devolución incrementadas - Ticket: {}, Técnico: {}",
                    ticket.getId(), tecnico.getId());

        } catch (Exception e) {
            logger.error("Error incrementando solicitudes de devolución: {}", e.getMessage(), e);
        }
    }

    /**
     * Actualizar devolución procesada
     */
    public void actualizarDevolucionProcesada(Ticket ticket, Tecnico tecnico, boolean aprobada) {
        try {
            LocalDateTime ahora = LocalDateTime.now();

            // Estadística general
            EstadisticaPeriodo estadistica = obtenerOCrearEstadisticaDiaria(ahora);
            if (aprobada) {
                estadistica.setDevolucionesAprobadas(estadistica.getDevolucionesAprobadas() + 1);
            } else {
                estadistica.setDevolucionesRechazadas(estadistica.getDevolucionesRechazadas() + 1);
            }
            estadisticaPeriodoRepository.save(estadistica);

            // Estadística del técnico
            EstadisticaTecnico estadisticaTecnico = obtenerOCrearEstadisticaTecnico(tecnico, ahora);
            if (aprobada) {
                estadisticaTecnico.setDevolucionesAprobadas(estadisticaTecnico.getDevolucionesAprobadas() + 1);
            }
            estadisticaTecnicoRepository.save(estadisticaTecnico);

            logger.debug("Devolución procesada actualizada - Ticket: {}, Técnico: {}, Aprobada: {}",
                    ticket.getId(), tecnico.getId(), aprobada);

        } catch (Exception e) {
            logger.error("Error actualizando devolución procesada: {}", e.getMessage(), e);
        }
    }

    /**
     * Incrementar tickets reabiertos
     */
    public void incrementarTicketsReabiertos(Ticket ticket) {
        try {
            LocalDateTime ahora = LocalDateTime.now();

            EstadisticaPeriodo estadistica = obtenerOCrearEstadisticaDiaria(ahora);
            estadistica.incrementarTicketsReabiertos();
            estadisticaPeriodoRepository.save(estadistica);

            logger.debug("Tickets reabiertos incrementados para ticket: {}", ticket.getId());

        } catch (Exception e) {
            logger.error("Error incrementando tickets reabiertos: {}", e.getMessage(), e);
        }
    }

    // ========================================
    // MÉTODOS AUXILIARES
    // ========================================

    private EstadisticaPeriodo obtenerOCrearEstadisticaDiaria(LocalDateTime fecha) {
        int anio = fecha.getYear();
        int mes = fecha.getMonthValue();
        int dia = fecha.getDayOfMonth();

        Optional<EstadisticaPeriodo> existente = estadisticaPeriodoRepository
                .findByPeriodoTipoAndAnioAndMesAndSemanaAndDiaAndTrimestre(
                        PeriodoTipo.DIARIO, anio, mes, null, dia, null);

        if (existente.isPresent()) {
            return existente.get();
        } else {
            EstadisticaPeriodo nueva = new EstadisticaPeriodo(PeriodoTipo.DIARIO, anio);
            nueva.setMes(mes);
            nueva.setDia(dia);
            return estadisticaPeriodoRepository.save(nueva);
        }
    }

    private EstadisticaPeriodo obtenerOCrearEstadisticaMensual(LocalDateTime fecha) {
        int anio = fecha.getYear();
        int mes = fecha.getMonthValue();

        Optional<EstadisticaPeriodo> existente = estadisticaPeriodoRepository
                .findByPeriodoTipoAndAnioAndMesAndSemanaAndDiaAndTrimestre(
                        PeriodoTipo.MENSUAL, anio, mes, null, null, null);

        if (existente.isPresent()) {
            return existente.get();
        } else {
            EstadisticaPeriodo nueva = new EstadisticaPeriodo(PeriodoTipo.MENSUAL, anio);
            nueva.setMes(mes);
            return estadisticaPeriodoRepository.save(nueva);
        }
    }

    private EstadisticaTecnico obtenerOCrearEstadisticaTecnico(Tecnico tecnico, LocalDateTime fecha) {
        int anio = fecha.getYear();
        int mes = fecha.getMonthValue();

        Optional<EstadisticaTecnico> existente = estadisticaTecnicoRepository
                .findByTecnicoAndPeriodoTipoAndAnioAndMesAndSemanaAndDiaAndTrimestre(
                        tecnico, PeriodoTipo.MENSUAL, anio, mes, null, null, null);

        if (existente.isPresent()) {
            return existente.get();
        } else {
            EstadisticaTecnico nueva = new EstadisticaTecnico(tecnico, PeriodoTipo.MENSUAL, anio);
            nueva.setMes(mes);
            return estadisticaTecnicoRepository.save(nueva);
        }
    }
}
