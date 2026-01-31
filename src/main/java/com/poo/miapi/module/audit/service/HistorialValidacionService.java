package com.poo.miapi.module.audit.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poo.miapi.module.audit.dto.HistorialValidacionRequestDto;
import com.poo.miapi.module.audit.dto.HistorialValidacionResponseDto;
import com.poo.miapi.module.audit.model.HistorialValidacion;
import com.poo.miapi.module.audit.repository.HistorialValidacionRepository;
import com.poo.miapi.module.ticket.model.Ticket;
import com.poo.miapi.module.ticket.repository.TicketRepository;
import com.poo.miapi.module.user.model.Trabajador;
import com.poo.miapi.module.user.repository.TrabajadorRepository;
import com.poo.miapi.shared.events.enums.AccionAuditoria;
import com.poo.miapi.shared.events.enums.CategoriaAuditoria;
import com.poo.miapi.shared.events.enums.SeveridadAuditoria;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
public class HistorialValidacionService {

        @Autowired
        private final HistorialValidacionRepository historialValidacionRepository;
        @Autowired
        private final TrabajadorRepository trabajadorRepository;
        @Autowired
        private final TicketRepository ticketRepository;
        private final AuditoriaService auditoriaService;

        public HistorialValidacionService(
                        HistorialValidacionRepository historialValidacionRepository,
                        TrabajadorRepository trabajadorRepository,
                        TicketRepository ticketRepository,
                        AuditoriaService auditoriaService) {
                this.historialValidacionRepository = historialValidacionRepository;
                this.trabajadorRepository = trabajadorRepository;
                this.ticketRepository = ticketRepository;
                this.auditoriaService = auditoriaService;
        }

        // MÉTODOS PÚBLICOS
        // Registrar validación desde DTO
        public HistorialValidacionResponseDto registrarValidacion(HistorialValidacionRequestDto dto) {
                // Aquí se debe buscar el usuario validador (puede ser Trabajador, Admin,
                // SuperAdmin)
                // Por ahora, se usa Trabajador como ejemplo:
                Trabajador usuarioValidador = trabajadorRepository.findById(dto.getIdUsuarioValidador())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Usuario validador no encontrado con ID: "
                                                                + dto.getIdUsuarioValidador()));
                Ticket ticket = ticketRepository.findById(dto.getIdTicket())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Ticket no encontrado con ID: " + dto.getIdTicket()));

                HistorialValidacion validacion = new HistorialValidacion(
                                usuarioValidador,
                                ticket,
                                dto.isFueAprobado(),
                                dto.getComentario());
                HistorialValidacion saved = historialValidacionRepository.save(validacion);

                // Auditar validación de ticket
                auditoriaService.registrarAccion(
                                usuarioValidador,
                                AccionAuditoria.EVALUATE_TICKET,
                                "VALIDACION_TICKET",
                                saved.getId(),
                                "Validación de ticket: " + (dto.isFueAprobado() ? "Aprobado" : "Rechazado") + " - "
                                                + dto.getComentario(),
                                null,
                                saved,
                                CategoriaAuditoria.BUSINESS,
                                dto.isFueAprobado() ? SeveridadAuditoria.LOW : SeveridadAuditoria.MEDIUM);

                return mapToDto(saved);
        }

        // Listar por trabajador como DTOs
        public List<HistorialValidacionResponseDto> listarPorUsuarioValidador(int usuarioValidadorId) {
                return historialValidacionRepository.findByUsuarioValidadorId(usuarioValidadorId).stream()
                                .map(this::mapToDto)
                                .toList();
        }

        // Listar por ticket como DTOs
        public List<HistorialValidacionResponseDto> listarPorTicket(int ticketId) {
                return historialValidacionRepository.findByTicketId(ticketId).stream()
                                .map(this::mapToDto)
                                .toList();
        }

        // Listar todos como DTOs
        public List<HistorialValidacionResponseDto> listarTodos() {
                return historialValidacionRepository.findAll().stream()
                                .map(this::mapToDto)
                                .toList();
        }

        // MÉTODOS PRIVADOS/UTILIDADES
        // Método auxiliar para mapear entidad a DTO
        private HistorialValidacionResponseDto mapToDto(HistorialValidacion validacion) {
                return new HistorialValidacionResponseDto(
                                validacion.getId(),
                                validacion.getUsuarioValidador().getId(),
                                validacion.getTicket().getId(),
                                validacion.isFueAprobado(),
                                validacion.getComentario(),
                                validacion.getFechaValidacion());
        }
}
