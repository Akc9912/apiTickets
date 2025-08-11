package com.poo.miapi.controller.core;

import com.poo.miapi.dto.ticket.TicketRequestDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.service.core.TicketService;
import com.poo.miapi.model.enums.EstadoTicket;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Controlador para gestión de tickets del sistema
 * 
 * NOTA IMPORTANTE sobre usuarios bloqueados:
 * Los usuarios bloqueados pueden iniciar sesión pero NO pueden realizar acciones.
 * Para verificar si un usuario puede realizar acciones, usar: usuario.puedeRealizarAcciones()
 * 
 * Ejemplo:
 * if (!usuarioActual.puedeRealizarAcciones()) {
 *     throw new IllegalStateException("Usuario bloqueado, no puede realizar acciones");
 * }
 */
@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "Endpoints para gestión de tickets del sistema")
public class TicketController {

        private final TicketService ticketService;
        private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TicketController.class);

        public TicketController(TicketService ticketService) {
                this.ticketService = ticketService;
        }

        // GET /api/tickets - Listar todos los tickets
        @GetMapping
        @Operation(summary = "Listar todos los tickets", description = "Obtiene una lista de todos los tickets del sistema")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de tickets obtenida exitosamente")
        })
        public List<TicketResponseDto> listarTickets() {
                logger.info("[TicketController] GET /api/tickets");
                List<TicketResponseDto> resp = ticketService.listarTodos();
                logger.info("[TicketController] Respuesta: {}", resp);
                return resp;
        }

        // GET /api/tickets/{id} - Ver detalle de un ticket
        @GetMapping("/{id}")
        @Operation(summary = "Ver detalle de ticket", description = "Obtiene los detalles de un ticket específico")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ticket encontrado exitosamente", content = @Content(schema = @Schema(implementation = TicketResponseDto.class))),
                        @ApiResponse(responseCode = "404", description = "Ticket no encontrado")
        })
        public TicketResponseDto verTicket(
                        @Parameter(description = "ID del ticket") @PathVariable int id) {
                logger.info("[TicketController] GET /api/tickets/{}", id);
                TicketResponseDto resp = ticketService.buscarPorId(id);
                logger.info("[TicketController] Respuesta: {}", resp);
                return resp;
        }

        // POST /api/tickets - Crear un nuevo ticket
        @PostMapping
        @Operation(summary = "Crear nuevo ticket", description = "Crea un nuevo ticket en el sistema")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ticket creado exitosamente", content = @Content(schema = @Schema(implementation = TicketResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos para crear el ticket")
        })
        public TicketResponseDto crearTicket(
                                @Parameter(description = "Datos del nuevo ticket") @RequestBody @Valid TicketRequestDto dto,
                                org.springframework.security.core.Authentication authentication) {

                                logger.info("[TicketController] POST /api/tickets datos: {}", dto);
                                // Obtener usuario autenticado
                                Object principal = authentication.getPrincipal();
                                String email = null;
                                if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
                                        email = userDetails.getUsername();
                                } else if (principal instanceof String) {
                                        email = (String) principal;
                                }
                                logger.debug("[TicketController] Principal: {}", principal);
                                logger.debug("[TicketController] Email extraído: {}", email);
                                if (email == null) {
                                        throw new IllegalStateException("No se pudo obtener el usuario autenticado");
                                }

                                // Obtener el usuario y su rol
                                com.poo.miapi.model.core.Usuario usuario = ticketService.obtenerUsuarioPorEmail(email);
                                logger.debug("[TicketController] Usuario obtenido: {} Rol: {}", usuario != null ? usuario.getEmail() : null, usuario != null ? usuario.getRol() : null);
                                logger.debug("[TicketController] DTO recibido: titulo={}, descripcion={}, idTrabajador={}", dto.getTitulo(), dto.getDescripcion(), dto.getIdTrabajador());
                                if (usuario == null || !usuario.puedeRealizarAcciones()) {
                                        throw new IllegalStateException("Usuario bloqueado o no encontrado");
                                }

                                // Lógica por rol
                                com.poo.miapi.model.core.Usuario creadorTicket = null;
                                logger.debug("[TicketController] Rol del usuario autenticado: {}", usuario.getRol().name());
                                switch (usuario.getRol().name()) {
                                        case "TRABAJADOR":
                                                creadorTicket = usuario;
                                                logger.debug("[TicketController] El creador del ticket será el usuario autenticado (TRABAJADOR): {}", usuario.getEmail());
                                                break;
                                        case "ADMIN":
                                        case "SUPERADMIN":
                                                // Si se especifica idTrabajador, crear para ese trabajador
                                                                                                if (dto.getIdTrabajador() != 0) {
                                                                                                        creadorTicket = ticketService.obtenerTrabajadorPorId(dto.getIdTrabajador());
                                                                                                        logger.debug("[TicketController] El creador del ticket será el trabajador con id {}", dto.getIdTrabajador());
                                                                                                        if (creadorTicket == null) {
                                                                                                                logger.error("[TicketController] Trabajador no encontrado para id {}", dto.getIdTrabajador());
                                                                                                                throw new IllegalArgumentException("Trabajador no encontrado");
                                                                                                        }
                                                                                                } else {
                                                                                                        // Si no, el admin/superadmin es el creador
                                                                                                        creadorTicket = usuario;
                                                                                                        logger.debug("[TicketController] El creador del ticket será el usuario autenticado (ADMIN/SUPERADMIN): {}", usuario.getEmail());
                                                                                                }
                                                break;
                                        default:
                                                throw new IllegalStateException("Rol no autorizado para crear tickets");
                                }

                                logger.debug("[TicketController] Llamando a TicketService.crearTicketConCreador con creador: {} (rol: {})", creadorTicket != null ? creadorTicket.getEmail() : null, creadorTicket != null ? creadorTicket.getRol() : null);
                                TicketResponseDto resp = ticketService.crearTicketConCreador(dto, creadorTicket);
                                logger.info("[TicketController] Respuesta: {}", resp);
                                return resp;
        }

        // PUT /api/tickets/{id}/estado - Actualizar estado de un ticket
        @PutMapping("/{id}/estado")
        @Operation(summary = "Actualizar estado del ticket", description = "Cambia el estado de un ticket existente")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente", content = @Content(schema = @Schema(implementation = TicketResponseDto.class))),
                        @ApiResponse(responseCode = "404", description = "Ticket no encontrado"),
                        @ApiResponse(responseCode = "400", description = "Estado inválido")
        })
        public TicketResponseDto actualizarEstado(
                        @Parameter(description = "ID del ticket") @PathVariable int id,
                        @Parameter(description = "Nuevo estado del ticket (NO_ATENDIDO, EN_PROCESO, RESUELTO, CERRADO)") @RequestParam String estado) {
                logger.info("[TicketController] PUT /api/tickets/{}/estado estado: {}", id, estado);
                TicketResponseDto resp = ticketService.actualizarEstado(id, EstadoTicket.valueOf(estado));
                logger.info("[TicketController] Respuesta: {}", resp);
                return resp;
        }

        // GET /api/tickets/estado?estado=NO_ATENDIDO - Listar tickets por estado
        @GetMapping("/estado")
        @Operation(summary = "Listar tickets por estado", description = "Obtiene tickets filtrados por estado específico")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de tickets filtrada exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Estado inválido")
        })
        public List<TicketResponseDto> listarPorEstado(
                        @Parameter(description = "Estado del ticket (NO_ATENDIDO, EN_PROCESO, RESUELTO, CERRADO)") @RequestParam String estado) {
                logger.info("[TicketController] GET /api/tickets/estado estado: {}", estado);
                List<TicketResponseDto> resp = ticketService.listarPorEstado(EstadoTicket.valueOf(estado));
                logger.info("[TicketController] Respuesta: {}", resp);
                return resp;
        }

        // GET /api/tickets/creador?userId=... - Listar tickets por creador
        @GetMapping("/creador")
        @Operation(summary = "Listar tickets por creador", description = "Obtiene todos los tickets creados por un usuario específico")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de tickets del creador obtenida exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        })
        public List<TicketResponseDto> listarPorCreador(
                        @Parameter(description = "ID del usuario creador") @RequestParam int userId) {
                                logger.info("[TicketController] GET /api/tickets/creador userId: {}", userId);
                                try {
                                        logger.debug("[TicketController] Buscando tickets por creador con id: {}", userId);
                                        List<TicketResponseDto> resp = ticketService.listarPorCreador(userId);
                                        logger.info("[TicketController] Respuesta: {}", resp);
                                        if (resp == null || resp.isEmpty()) {
                                                logger.warn("[TicketController] No se encontraron tickets para el creador con id: {}", userId);
                                        }
                                        return resp;
                                } catch (Exception e) {
                                        logger.error("[TicketController] Error al obtener tickets por creador: {}", e.getMessage(), e);
                                        throw e;
                                }
        }

        // GET /api/tickets/buscar-titulo?palabra=... - Buscar tickets por título
        @GetMapping("/buscar-titulo")
        @Operation(summary = "Buscar tickets por título", description = "Busca tickets que contengan una palabra específica en el título")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente")
        })
        public List<TicketResponseDto> buscarPorTitulo(
                        @Parameter(description = "Palabra a buscar en el título del ticket") @RequestParam String palabra) {
                logger.info("[TicketController] GET /api/tickets/buscar-titulo palabra: {}", palabra);
                List<TicketResponseDto> resp = ticketService.buscarPorTitulo(palabra);
                logger.info("[TicketController] Respuesta: {}", resp);
                return resp;
        }
}
