package com.poo.miapi.service.historial;

import com.poo.miapi.dto.tecnico.IncidenteTecnicoResponseDto;
import com.poo.miapi.dto.tecnico.IncidenteTecnicoRequestDto;
import com.poo.miapi.model.historial.IncidenteTecnico;
import com.poo.miapi.model.core.Tecnico;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.repository.core.TecnicoRepository;
import com.poo.miapi.repository.core.TicketRepository;
import com.poo.miapi.repository.historial.IncidenteTecnicoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import com.poo.miapi.service.auditoria.AuditoriaService;
import com.poo.miapi.model.enums.AccionAuditoria;
import com.poo.miapi.model.enums.CategoriaAuditoria;
import com.poo.miapi.model.enums.SeveridadAuditoria;
import com.poo.miapi.model.enums.TipoIncidente;

import java.util.List;

@Service
public class IncidenteTecnicoService {

        @Autowired
        private final IncidenteTecnicoRepository incidenteTecnicoRepository;
        @Autowired
        private final TecnicoRepository tecnicoRepository;
        @Autowired
        private final TicketRepository ticketRepository;
        private final AuditoriaService auditoriaService;

        public IncidenteTecnicoService(
                        IncidenteTecnicoRepository incidenteTecnicoRepository,
                        TecnicoRepository tecnicoRepository,
                        TicketRepository ticketRepository,
                        AuditoriaService auditoriaService) {
                this.incidenteTecnicoRepository = incidenteTecnicoRepository;
                this.tecnicoRepository = tecnicoRepository;
                this.ticketRepository = ticketRepository;
                this.auditoriaService = auditoriaService;
        }

        // MÉTODOS PÚBLICOS
        // Listar todos los incidentes como DTOs
        public List<IncidenteTecnicoResponseDto> listarTodos() {
                return incidenteTecnicoRepository.findAll().stream()
                                .map(this::mapToDto)
                                .toList();
        }

        // Listar incidentes por técnico como DTOs
        public List<IncidenteTecnicoResponseDto> listarPorTecnico(int idTecnico) {
                return incidenteTecnicoRepository.findByTecnicoId(idTecnico).stream()
                                .map(this::mapToDto)
                                .toList();
        }

        // Listar incidentes por ticket como DTOs
        public List<IncidenteTecnicoResponseDto> listarPorTicket(int idTicket) {
                return incidenteTecnicoRepository.findByTicketId(idTicket).stream()
                                .map(this::mapToDto)
                                .toList();
        }

        // Listar incidentes por tipo
        public List<IncidenteTecnicoResponseDto> listarPorTipo(TipoIncidente tipo) {
                return incidenteTecnicoRepository.findByTipo(tipo).stream()
                                .map(this::mapToDto)
                                .toList();
        }

        // Contar incidentes por técnico
        public int contarPorTecnico(int idTecnico) {
                return incidenteTecnicoRepository.countByTecnicoId(idTecnico);
        }

        // Registrar incidente desde DTO
        public IncidenteTecnicoResponseDto registrarIncidente(IncidenteTecnicoRequestDto dto) {
                Tecnico tecnico = tecnicoRepository.findById(dto.getIdTecnico())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Técnico no encontrado con ID: " + dto.getIdTecnico()));
                Ticket ticket = ticketRepository.findById(dto.getIdTicket())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Ticket no encontrado con ID: " + dto.getIdTicket()));

                IncidenteTecnico incidente = new IncidenteTecnico(
                                tecnico,
                                ticket,
                                dto.getTipo(),
                                dto.getMotivo());
                IncidenteTecnico saved = incidenteTecnicoRepository.save(incidente);

                // Auditar registro de incidente
                auditoriaService.registrarAccion(
                                null, // No tenemos usuario que ejecuta la acción
                                AccionAuditoria.CREATE_INCIDENT,
                                "INCIDENTE_TECNICO",
                                saved.getId(),
                                "Incidente registrado para técnico " + tecnico.getNombre() + ": " + dto.getMotivo(),
                                null,
                                saved,
                                CategoriaAuditoria.BUSINESS,
                                dto.getTipo() == TipoIncidente.FALLA ? SeveridadAuditoria.HIGH
                                                : SeveridadAuditoria.MEDIUM);

                return mapToDto(saved);
        }

        // MÉTODOS PRIVADOS/UTILIDADES
        // Método auxiliar para mapear entidad a DTO
        private IncidenteTecnicoResponseDto mapToDto(IncidenteTecnico incidente) {
                return new IncidenteTecnicoResponseDto(
                                incidente.getId(),
                                incidente.getTecnico().getId(),
                                incidente.getTicket().getId(),
                                incidente.getTipo(),
                                incidente.getMotivo(),
                                incidente.getFechaRegistro());
        }
}
