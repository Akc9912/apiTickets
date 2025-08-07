package com.poo.miapi.controller.core;

import com.poo.miapi.dto.ticket.EvaluarTicketDto;
import com.poo.miapi.dto.ticket.TicketRequestDto;
import com.poo.miapi.dto.ticket.TicketResponseDto;
import com.poo.miapi.dto.trabajador.TrabajadorResponseDto;
import com.poo.miapi.service.core.TrabajadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/trabajador")
@Tag(name = "Trabajadores", description = "Endpoints para gestión de trabajadores y creación de tickets")
public class TrabajadorController {

    private final TrabajadorService trabajadorService;

    public TrabajadorController(TrabajadorService trabajadorService) {
        this.trabajadorService = trabajadorService;
    }

    // POST /api/trabajador/tickets - Crear un nuevo ticket
    @PostMapping("/tickets")
    @Operation(summary = "Crear nuevo ticket", description = "Permite a un trabajador crear un nuevo ticket de soporte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket creado exitosamente", content = @Content(schema = @Schema(implementation = TicketResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos para crear el ticket")
    })
    public TicketResponseDto crearTicket(
            @Parameter(description = "Datos del nuevo ticket") @RequestBody @Valid TicketRequestDto dto) {
        return trabajadorService.crearTicket(dto);
    }

    // GET /api/trabajador/tickets - Ver todos mis tickets
    @GetMapping("/tickets")
    @Operation(summary = "Ver todos mis tickets", description = "Obtiene todos los tickets creados por un trabajador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tickets obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Trabajador no encontrado")
    })
    public List<TicketResponseDto> verTodosMisTickets(
            @Parameter(description = "ID del trabajador") @RequestParam int idTrabajador) {
        return trabajadorService.verTodosMisTickets(idTrabajador);
    }

    // GET /api/trabajador/tickets/activos - Ver mis tickets activos
    @GetMapping("/tickets/activos")
    @Operation(summary = "Ver tickets activos", description = "Obtiene los tickets activos (no finalizados) de un trabajador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tickets activos obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Trabajador no encontrado")
    })
    public List<TicketResponseDto> verTicketsActivos(
            @Parameter(description = "ID del trabajador") @RequestParam int idTrabajador) {
        return trabajadorService.verTicketsActivos(idTrabajador);
    }

    // POST /api/trabajador/tickets/{ticketId}/evaluar - Evaluar la atención
    // recibida en un ticket
    @PostMapping("/tickets/{ticketId}/evaluar")
    @Operation(summary = "Evaluar ticket", description = "Permite al trabajador evaluar la atención recibida en un ticket finalizado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket evaluado exitosamente", content = @Content(schema = @Schema(implementation = TicketResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado"),
            @ApiResponse(responseCode = "400", description = "El ticket no está en estado finalizado o ya fue evaluado")
    })
    public TicketResponseDto evaluarTicket(
            @Parameter(description = "ID del ticket") @PathVariable int ticketId,
            @Parameter(description = "Datos de la evaluación") @RequestBody EvaluarTicketDto dto) {
        return trabajadorService.evaluarTicket(ticketId, dto);
    }

    // GET /api/trabajador/listar-todos - Listar todos los trabajadores (admin)
    @GetMapping("/listar-todos")
    @Operation(summary = "Listar todos los trabajadores", description = "Obtiene una lista de todos los trabajadores (función administrativa)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de trabajadores obtenida exitosamente")
    })
    public List<TrabajadorResponseDto> listarTodos() {
        return trabajadorService.listarTodos();
    }
}
