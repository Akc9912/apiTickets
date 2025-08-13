package com.poo.miapi.controller.core;

import com.poo.miapi.dto.ticket.TicketRequestDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.service.core.TicketService;
import com.poo.miapi.model.core.Usuario;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.AccessDeniedException;
import jakarta.validation.Valid;
import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "Endpoints para gestión de tickets del sistema")
public class TicketController {
        private static final Logger logger = LoggerFactory.getLogger(TicketController.class);


        private final TicketService ticketService;

        public TicketController(TicketService ticketService) {
                this.ticketService = ticketService;
        }

        // 1. /api/tickets/todos - Admin/SuperAdmin: ver todos los tickets
        @Operation(summary = "Listar todos los tickets", description = "Devuelve todos los tickets del sistema. Solo Admin/SuperAdmin.")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de tickets", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
        })
        @GetMapping("/todos")
        public List<TicketResponseDto> listarTodosTicketsAdmin(Authentication authentication) {
                Usuario usuario = ticketService.obtenerUsuarioPorEmail(authentication.getName());
                if (usuario == null || !(usuario.getRol().name().equals("ADMIN") || usuario.getRol().name().equals("SUPERADMIN"))) {
                        throw new AccessDeniedException("No autorizado");
                }
                return ticketService.listarTodos();
        }

        // 2. /api/tickets/trabajador/mis-tickets - Trabajador: ver sus tickets
        @Operation(summary = "Listar mis tickets (Trabajador)", description = "Devuelve los tickets creados por el trabajador autenticado.")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de tickets", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
        })
        @GetMapping("/trabajador/mis-tickets")
        public List<TicketResponseDto> listarMisTicketsTrabajador(Authentication authentication) {
                Usuario usuario = ticketService.obtenerUsuarioPorEmail(authentication.getName());
                if (usuario == null || !usuario.getRol().name().equals("TRABAJADOR")) {
                        throw new AccessDeniedException("No autorizado");
                }
                return ticketService.listarPorCreador(usuario.getId());
        }

        // 3. /api/tickets/tecnico/tickets-disponibles - Técnico: tickets no asignados y reabiertos
        @Operation(summary = "Listar tickets disponibles (Técnico)", description = "Devuelve tickets no asignados y reabiertos para técnicos.")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de tickets", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
        })
        @GetMapping("/tecnico/tickets-disponibles")
        public List<TicketResponseDto> listarTicketsDisponiblesTecnico(Authentication authentication) {
                Usuario usuario = ticketService.obtenerUsuarioPorEmail(authentication.getName());
                if (usuario == null || !usuario.getRol().name().equals("TECNICO")) {
                        throw new AccessDeniedException("No autorizado");
                }
                return ticketService.listarTicketsNoAsignadosYReabiertos();
        }

        // 4. /api/tickets/tecnico/mis-tickets - Técnico: sus tickets en estado atendido o resuelto
        @Operation(summary = "Listar mis tickets (Técnico)", description = "Devuelve los tickets asignados al técnico autenticado en estado atendido o resuelto.")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de tickets", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
        })
        @GetMapping("/tecnico/mis-tickets")
        public List<TicketResponseDto> listarMisTicketsTecnico(Authentication authentication) {
                Usuario usuario = ticketService.obtenerUsuarioPorEmail(authentication.getName());
                if (usuario == null || !usuario.getRol().name().equals("TECNICO")) {
                        throw new AccessDeniedException("No autorizado");
                }
                return ticketService.listarTicketsAsignadosAlTecnico(usuario.getId());
        }

        // 5. /api/tickets/tecnico/historial - Técnico: historial de tickets donde participó
        @Operation(summary = "Historial de tickets (Técnico)", description = "Devuelve el historial de tickets donde el técnico autenticado participó.")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de tickets", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
        })
        @GetMapping("/tecnico/historial")
        public List<TicketResponseDto> listarHistorialTecnico(Authentication authentication) {
                Usuario usuario = ticketService.obtenerUsuarioPorEmail(authentication.getName());
                if (usuario == null || !usuario.getRol().name().equals("TECNICO")) {
                        throw new AccessDeniedException("No autorizado");
                }
                return ticketService.listarHistorialTecnico(usuario.getId());
        }

        // 6. /api/tickets/crear-ticket Endpoint para crear ticket
        @Operation(summary = "Crear ticket", description = "Permite crear un ticket según el rol del usuario autenticado.")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ticket creado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content)
        })
        @PostMapping("/crear-ticket")
        public TicketResponseDto crearTicket(@RequestBody @Valid TicketRequestDto dto, Authentication authentication) {
                
                logger.info("Authentication recibido: {}", authentication);
                if (authentication != null) {
                        logger.info("Principal: {}", authentication.getPrincipal());
                }

                Object principal = authentication != null ? authentication.getPrincipal() : null;

                String email = null;
                if (principal instanceof UserDetails userDetails) {
                        email = userDetails.getUsername();
                } else if (principal instanceof String) {
                        email = (String) principal;
                }
                if (email == null) {
                        throw new IllegalStateException("No se pudo obtener el usuario autenticado");
                }
                Usuario usuario = ticketService.obtenerUsuarioPorEmail(email);
                if (usuario == null || !usuario.puedeRealizarAcciones()) {
                        throw new IllegalStateException("Usuario bloqueado o no encontrado");
                }
                return ticketService.crearTicketConRol(dto, usuario);
        }

        // POST /api/tickets/{id}/reabrir
        @Operation(summary = "Reabrir ticket", description = "Permite reabrir un ticket según el rol del usuario autenticado.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Ticket reabierto exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponseDto.class))),
                @ApiResponse(responseCode = "404", description = "Ticket no encontrado", content = @Content),
                @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content),
                @ApiResponse(responseCode = "400", description = "El ticket no puede ser reabierto", content = @Content)
        })
        @PostMapping("/{id}/reabrir")
        public TicketResponseDto reabrirTicket(@PathVariable int id, @RequestParam String comentario, Authentication authentication) {
                        Usuario usuario = ticketService.obtenerUsuarioPorEmail(authentication.getName());
                        if (usuario == null || !usuario.puedeRealizarAcciones()) {
                                throw new AccessDeniedException("Usuario bloqueado o no encontrado");
                        }
                        return ticketService.reabrirTicket(id, comentario, usuario.getId());
        }

}
        