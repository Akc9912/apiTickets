package com.poo.miapi.service.estadistica;

import com.poo.miapi.model.estadistica.EstadisticaPeriodo;
import com.poo.miapi.model.estadistica.EstadisticaTecnico;
import com.poo.miapi.model.estadistica.EstadisticaUsuario;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.model.enums.PeriodoTipo;
import com.poo.miapi.repository.estadistica.EstadisticaPeriodoRepository;
import com.poo.miapi.repository.estadistica.EstadisticaTecnicoRepository;
import com.poo.miapi.repository.estadistica.EstadisticaUsuarioRepository;

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
    private final EstadisticaUsuarioRepository estadisticaUsuarioRepository;

    public EstadisticaActualizadorService(
            EstadisticaPeriodoRepository estadisticaPeriodoRepository,
            EstadisticaTecnicoRepository estadisticaTecnicoRepository,
            EstadisticaUsuarioRepository estadisticaUsuarioRepository) {
        this.estadisticaPeriodoRepository = estadisticaPeriodoRepository;
        this.estadisticaTecnicoRepository = estadisticaTecnicoRepository;
        this.estadisticaUsuarioRepository = estadisticaUsuarioRepository;
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

    // ========================================
    // NUEVOS MÉTODOS EXPANDIDOS - FASE 2
    // ========================================

    /**
     * Incrementar tickets creados (cuando un usuario crea un ticket)
     */
    public void incrementarTicketsCreados(Ticket ticket, Usuario creador) {
        try {
            LocalDateTime ahora = LocalDateTime.now();

            // Actualizar estadística general
            EstadisticaPeriodo estadistica = obtenerOCrearEstadisticaDiaria(ahora);
            estadistica.incrementarTicketsCreados();
            estadisticaPeriodoRepository.save(estadistica);

            // Actualizar estadística del usuario creador
            actualizarEstadisticasUsuarioCreacion(creador, ahora);

            logger.debug("Tickets creados incrementados para ticket: {} por usuario: {}",
                    ticket.getId(), creador.getId());

        } catch (Exception e) {
            logger.error("Error incrementando tickets creados: {}", e.getMessage(), e);
        }
    }

    /**
     * Incrementar tickets asignados (cuando un admin asigna un ticket)
     */
    public void incrementarTicketsAsignados(Ticket ticket, Tecnico tecnico, Usuario asignador) {
        try {
            LocalDateTime ahora = LocalDateTime.now();

            // Actualizar estadística general
            EstadisticaPeriodo estadistica = obtenerOCrearEstadisticaDiaria(ahora);
            // No hay método específico, pero podemos usar los existentes
            estadisticaPeriodoRepository.save(estadistica);

            // Actualizar estadística del técnico
            EstadisticaTecnico estadisticaTecnico = obtenerOCrearEstadisticaTecnico(tecnico, ahora);
            estadisticaTecnico.setTicketsAsignados(estadisticaTecnico.getTicketsAsignados() + 1);
            estadisticaTecnicoRepository.save(estadisticaTecnico);

            // Actualizar estadística del asignador (Admin/SuperAdmin)
            actualizarEstadisticasUsuarioAsignacion(asignador, ahora);

            logger.debug("Ticket asignado: {} → Técnico: {} por: {}",
                    ticket.getId(), tecnico.getId(), asignador.getId());

        } catch (Exception e) {
            logger.error("Error procesando asignación de ticket: {}", e.getMessage(), e);
        }
    }

    /**
     * Registrar inicio de sesión de usuario
     */
    public void registrarInicioSesion(Usuario usuario) {
        try {
            LocalDateTime ahora = LocalDateTime.now();

            // Actualizar estadística del usuario
            actualizarEstadisticasUsuarioLogin(usuario, ahora);

            logger.debug("Inicio de sesión registrado para usuario: {}", usuario.getId());

        } catch (Exception e) {
            logger.error("Error registrando inicio de sesión: {}", e.getMessage(), e);
        }
    }

    /**
     * Registrar cierre de sesión con tiempo conectado
     */
    public void registrarCierreSesion(Usuario usuario, int minutosSesion) {
        try {
            LocalDateTime ahora = LocalDateTime.now();

            // Actualizar estadística del usuario
            actualizarEstadisticasUsuarioLogout(usuario, ahora, minutosSesion);

            logger.debug("Cierre de sesión registrado para usuario: {} ({}m conectado)",
                    usuario.getId(), minutosSesion);

        } catch (Exception e) {
            logger.error("Error registrando cierre de sesión: {}", e.getMessage(), e);
        }
    }

    /**
     * Incrementar usuarios gestionados (cuando un admin crea un usuario)
     */
    public void incrementarUsuariosGestionados(Usuario adminCreador, Usuario usuarioCreado) {
        try {
            LocalDateTime ahora = LocalDateTime.now();

            // Solo para admins y superadmins
            if (adminCreador.getRol().isAdminRole()) {
                actualizarEstadisticasUsuarioGestion(adminCreador, ahora);

                logger.debug("Usuario gestionado incrementado - Admin: {} creó usuario: {}",
                        adminCreador.getId(), usuarioCreado.getId());
            }

        } catch (Exception e) {
            logger.error("Error incrementando usuarios gestionados: {}", e.getMessage(), e);
        }
    }

    /**
     * Registrar generación de reporte
     */
    public void registrarReporteGenerado(Usuario generador, String tipoReporte) {
        try {
            LocalDateTime ahora = LocalDateTime.now();

            actualizarEstadisticasUsuarioReporte(generador, ahora, tipoReporte);

            logger.debug("Reporte generado - Usuario: {}, Tipo: {}",
                    generador.getId(), tipoReporte);

        } catch (Exception e) {
            logger.error("Error registrando generación de reporte: {}", e.getMessage(), e);
        }
    }

    // ========================================
    // MÉTODOS AUXILIARES PARA ESTADÍSTICAS DE USUARIO
    // ========================================

    private void actualizarEstadisticasUsuarioCreacion(Usuario usuario, LocalDateTime fecha) {
        EstadisticaUsuario estadistica = obtenerOCrearEstadisticaUsuario(usuario, fecha);
        estadistica.incrementarTicketsCreados();
        estadisticaUsuarioRepository.save(estadistica);
    }

    private void actualizarEstadisticasUsuarioAsignacion(Usuario asignador, LocalDateTime fecha) {
        EstadisticaUsuario estadistica = obtenerOCrearEstadisticaUsuario(asignador, fecha);
        estadistica.setTicketsAsignados(estadistica.getTicketsAsignados() + 1);
        estadistica.actualizarFechaModificacion();
        estadisticaUsuarioRepository.save(estadistica);
    }

    private void actualizarEstadisticasUsuarioLogin(Usuario usuario, LocalDateTime fecha) {
        EstadisticaUsuario estadistica = obtenerOCrearEstadisticaUsuario(usuario, fecha);
        estadistica.setSesionesActivas(estadistica.getSesionesActivas() + 1);
        estadistica.actualizarFechaModificacion();
        estadisticaUsuarioRepository.save(estadistica);
    }

    private void actualizarEstadisticasUsuarioLogout(Usuario usuario, LocalDateTime fecha, int minutosSesion) {
        EstadisticaUsuario estadistica = obtenerOCrearEstadisticaUsuario(usuario, fecha);
        estadistica.setSesionesActivas(Math.max(0, estadistica.getSesionesActivas() - 1));
        estadistica.setTiempoConectadoMinutos(estadistica.getTiempoConectadoMinutos() + minutosSesion);
        estadistica.actualizarFechaModificacion();
        estadisticaUsuarioRepository.save(estadistica);
    }

    private void actualizarEstadisticasUsuarioGestion(Usuario admin, LocalDateTime fecha) {
        EstadisticaUsuario estadistica = obtenerOCrearEstadisticaUsuario(admin, fecha);
        estadistica.setUsuariosGestionados(estadistica.getUsuariosGestionados() + 1);
        estadistica.actualizarFechaModificacion();
        estadisticaUsuarioRepository.save(estadistica);
    }

    private void actualizarEstadisticasUsuarioReporte(Usuario generador, LocalDateTime fecha, String tipoReporte) {
        EstadisticaUsuario estadistica = obtenerOCrearEstadisticaUsuario(generador, fecha);
        estadistica.setReportesGenerados(estadistica.getReportesGenerados() + 1);
        estadistica.actualizarFechaModificacion();
        estadisticaUsuarioRepository.save(estadistica);
    }

    private EstadisticaUsuario obtenerOCrearEstadisticaUsuario(Usuario usuario, LocalDateTime fecha) {
        int anio = fecha.getYear();
        int mes = fecha.getMonthValue();

        Optional<EstadisticaUsuario> existente = estadisticaUsuarioRepository
                .findByUsuarioAndPeriodoTipoAndAnioAndMesAndSemanaAndDiaAndTrimestre(
                        usuario, PeriodoTipo.MENSUAL, anio, mes, null, null, null);

        if (existente.isPresent()) {
            return existente.get();
        } else {
            EstadisticaUsuario nueva = new EstadisticaUsuario(usuario, PeriodoTipo.MENSUAL, anio);
            nueva.setMes(mes);
            return estadisticaUsuarioRepository.save(nueva);
        }
    }
}
