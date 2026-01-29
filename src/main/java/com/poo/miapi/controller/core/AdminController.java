package com.poo.miapi.controller.core;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.poo.miapi.dto.historial.ProcesarSolicitudDevolucionRequestDto;
import com.poo.miapi.dto.historial.SolicitudDevolucionResponseDto;
import com.poo.miapi.dto.usuarios.UsuarioRequestDto;
import com.poo.miapi.dto.usuarios.UsuarioResponseDto;
import com.poo.miapi.model.core.Ticket;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.repository.core.TicketRepository;
import com.poo.miapi.service.core.UsuarioService;
import com.poo.miapi.service.historial.SolicitudDevolucionService;
import com.poo.miapi.service.notificacion.motor.EventPublisherService;
import com.poo.miapi.model.core.Usuario;
import com.poo.miapi.model.historial.SolicitudDevolucion;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administradores", description = "Endpoints para gestión administrativa del sistema")
public class AdminController {

    private final UsuarioService usuarioService;
    private final SolicitudDevolucionService solicitudDevolucionService;
    private final EventPublisherService eventPublisherService;
    private final TicketRepository ticketRepository;

    public AdminController(UsuarioService usuarioService, SolicitudDevolucionService solicitudDevolucionService,
            EventPublisherService eventPublisherService, TicketRepository ticketRepository) {
        this.usuarioService = usuarioService;
        this.solicitudDevolucionService = solicitudDevolucionService;
        this.eventPublisherService = eventPublisherService;
        this.ticketRepository = ticketRepository;
    }

    // procesar solicitud de devolución - POST
    // /api/admin/solicitudes-devolucion/{solicitudId}/procesar
    @PostMapping("/solicitudes-devolucion/{solicitudId}/procesar")
    public ResponseEntity<SolicitudDevolucionResponseDto> procesarSolicitudDevolucion(
            @PathVariable int solicitudId,
            @RequestBody ProcesarSolicitudDevolucionRequestDto requestDto) {
        try {
            // 1. Procesar solicitud (lógica de negocio existente)
            SolicitudDevolucionResponseDto dto = solicitudDevolucionService.procesarSolicitudDevolucion(solicitudId,
                    requestDto.getIdAdmin(), requestDto.isAprobar(), requestDto.getComentario());

            // 2. Publicar evento para notificación automática
            Ticket ticket = ticketRepository.findById(dto.getIdTicket())
                    .orElseThrow(() -> new EntityNotFoundException("Ticket no encontrado"));
            Usuario tecnico = usuarioService.buscarPorId(dto.getIdTecnico());
            Usuario admin = usuarioService.buscarPorId(requestDto.getIdAdmin());
            Usuario trabajador = ticket.getCreador(); // El creador del ticket (trabajador)

            eventPublisherService.publicarDevolucionProcesada(ticket, admin, tecnico, trabajador,
                    requestDto.isAprobar(),
                    requestDto.getComentario());

            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    // PUT /api/admin/usuarios/{id}
    @PutMapping("/usuarios/{id}")
    @Operation(summary = "Editar usuario", description = "Edita los datos de un usuario existente", parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "id", description = "ID del usuario a editar", required = true)
    }, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioRequestDto.class))), responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario actualizado", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioResponseDto.class)))
    })
    public ResponseEntity<UsuarioResponseDto> editarUsuario(@PathVariable int id,
            @RequestBody UsuarioRequestDto usuarioDto) {
        UsuarioResponseDto usuarioActualizado = usuarioService.editarDatosUsuario(id, usuarioDto);
        return ResponseEntity.ok(usuarioActualizado);
    }

    // PUT /api/admin/usuarios/{id}/activar
    @PutMapping("/usuarios/{id}/activar")
    @Operation(summary = "Activar usuario", description = "Activa o desactiva el usuario (toggle)", parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "id", description = "ID del usuario a activar/desactivar", required = true)
    }, responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario actualizado", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioResponseDto.class)))
    })
    public ResponseEntity<UsuarioResponseDto> activarUsuario(@PathVariable int id) {
        UsuarioResponseDto resp = usuarioService.setUsuarioActivo(id);
        return ResponseEntity.ok(resp);
    }

    // PUT /api/admin/usuarios/{id}/bloquear
    @PutMapping("/usuarios/{id}/bloquear")
    @Operation(summary = "Bloquear usuario", description = "Bloquea o desbloquea el usuario (toggle)", parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "id", description = "ID del usuario a bloquear/desbloquear", required = true)
    }, responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario actualizado", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioResponseDto.class)))
    })
    public ResponseEntity<UsuarioResponseDto> bloquearUsuario(@PathVariable int id) {
        UsuarioResponseDto resp = usuarioService.setUsuarioBloqueado(id);
        return ResponseEntity.ok(resp);
    }

    // PUT /api/admin/usuarios/{id}/rol
    @PutMapping("/usuarios/{id}/rol")
    @Operation(summary = "Cambiar rol de usuario", description = "Cambia el rol de un usuario existente", parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "id", description = "ID del usuario a cambiar rol", required = true)
    }, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioRequestDto.class))), responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rol cambiado", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioResponseDto.class)))
    })
    public ResponseEntity<UsuarioResponseDto> cambiarRolUsuario(@PathVariable int id,
            @RequestBody UsuarioRequestDto cambiarRolDto) {
        UsuarioResponseDto resp = usuarioService.cambiarRolUsuario(id, cambiarRolDto);
        return ResponseEntity.ok(resp);
    }

    // GET /api/admin/listar-usuarios
    @GetMapping("/listar-usuarios")
    @Operation(summary = "Listar todos los usuarios", description = "Devuelve la lista de todos los usuarios del sistema. Solo Admin y SuperAdmin pueden acceder. Admin no puede ver SuperAdmins.")
    public ResponseEntity<List<UsuarioResponseDto>> listarUsuarios(
            @AuthenticationPrincipal Usuario usuarioAutenticado) {
        try {
            List<UsuarioResponseDto> usuariosRaw = usuarioService.listarTodosFiltrado(usuarioAutenticado);
            List<UsuarioResponseDto> usuarios = usuariosRaw.stream()
                    .map(u -> (UsuarioResponseDto) u)
                    .toList();
            return ResponseEntity.ok(usuarios);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(null);
        }
    }

    // ver solicitudes de devolución - GET /api/admin/solicitudes-devolucion
    @GetMapping("/solicitudes-devolucion")
    public ResponseEntity<List<SolicitudDevolucionResponseDto>> verSolicitudesDevolucion() {
        List<SolicitudDevolucion> solicitudes = solicitudDevolucionService.verSolicitudesDevolucion();
        List<SolicitudDevolucionResponseDto> dtos = solicitudes.stream()
                .map(solicitudDevolucionService::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // procesar solicitud de devolución - PUT /api/admin/solicitudes-devolucion
    @PutMapping("/solicitudes-devolucion")
    public ResponseEntity<SolicitudDevolucionResponseDto> procesarSolicitudDevolucion(
            @RequestParam int solicitudId,
            @RequestParam int idTecnico,
            @RequestParam boolean aprobar,
            @RequestParam String comentario) {
        try {
            SolicitudDevolucionResponseDto dto = solicitudDevolucionService.procesarSolicitudDevolucion(solicitudId,
                    idTecnico, aprobar, comentario);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(null);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(null);
        }
    }
}
