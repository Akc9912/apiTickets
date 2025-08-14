package com.poo.miapi.service.historial;

import com.poo.miapi.dto.historial.HistorialValidacionResponseDto;
import com.poo.miapi.dto.historial.HistorialValidacionRequestDto;
import com.poo.miapi.model.historial.HistorialValidacion;
import com.poo.miapi.model.core.Trabajador;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.repository.core.TicketRepository;
import com.poo.miapi.repository.core.TrabajadorRepository;
import com.poo.miapi.repository.historial.HistorialValidacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

        public HistorialValidacionService(
                        HistorialValidacionRepository historialValidacionRepository,
                        TrabajadorRepository trabajadorRepository,
                        TicketRepository ticketRepository) {
                this.historialValidacionRepository = historialValidacionRepository;
                this.trabajadorRepository = trabajadorRepository;
                this.ticketRepository = ticketRepository;
        }

        // Registrar validación desde DTO
        public HistorialValidacionResponseDto registrarValidacion(HistorialValidacionRequestDto dto) {
                // Aquí se debe buscar el usuario validador (puede ser Trabajador, Admin, SuperAdmin)
                // Por ahora, se usa Trabajador como ejemplo:
                Trabajador usuarioValidador = trabajadorRepository.findById(dto.getIdUsuarioValidador())
                                .orElseThrow(() -> new EntityNotFoundException("Usuario validador no encontrado con ID: " + dto.getIdUsuarioValidador()));
                Ticket ticket = ticketRepository.findById(dto.getIdTicket())
                                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado con ID: " + dto.getIdTicket()));

                HistorialValidacion validacion = new HistorialValidacion(
                                usuarioValidador,
                                ticket,
                                dto.isFueAprobado(),
                                dto.getComentario());
                HistorialValidacion saved = historialValidacionRepository.save(validacion);
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