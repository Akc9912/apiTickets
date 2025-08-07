package com.poo.miapi.service.historial;

import com.poo.miapi.dto.historial.HistorialValidacionResponseDto;
import com.poo.miapi.dto.historial.HistorialValidacionRequestDto;
import com.poo.miapi.model.historial.HistorialValidacionTrabajador;
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
                Trabajador trabajador = trabajadorRepository.findById(dto.getIdTrabajador())
                                .orElseThrow(() -> new EntityNotFoundException("Trabajador no encontrado con ID: " + dto.getIdTrabajador()));
                Ticket ticket = ticketRepository.findById(dto.getIdTicket())
                                .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado con ID: " + dto.getIdTicket()));

                HistorialValidacionTrabajador validacion = new HistorialValidacionTrabajador(
                                trabajador,
                                ticket,
                                dto.isFueResuelto(),
                                dto.getComentario());
                HistorialValidacionTrabajador saved = historialValidacionRepository.save(validacion);
                return mapToDto(saved);
        }

        // Listar por trabajador como DTOs
        public List<HistorialValidacionResponseDto> listarPorTrabajador(int trabajadorId) {
                return historialValidacionRepository.findByTrabajadorId(trabajadorId).stream()
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
        private HistorialValidacionResponseDto mapToDto(HistorialValidacionTrabajador validacion) {
                return new HistorialValidacionResponseDto(
                                validacion.getId(),
                                validacion.getTrabajador().getId(),
                                validacion.getTicket().getId(),
                                validacion.isFueResuelto(),
                                validacion.getComentario(),
                                validacion.getFechaRegistro());
        }
}