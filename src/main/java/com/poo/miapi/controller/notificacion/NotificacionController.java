package com.poo.miapi.controller.notificacion;

import com.poo.miapi.dto.notificacion.NotificacionResponseDto;
import com.poo.miapi.dto.notificacion.NotificacionCreateDto;
import com.poo.miapi.service.notificacion.NotificacionService;
import com.poo.miapi.service.core.UsuarioService;
import com.poo.miapi.model.core.Usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones", description = "Endpoints para gestión del sistema de notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final UsuarioService usuarioService;

    public NotificacionController(NotificacionService notificacionService, UsuarioService usuarioService) {
        this.notificacionService = notificacionService;
        this.usuarioService = usuarioService;
    }

    // Obtener todas las notificaciones del usuario autenticado
    @Operation(summary = "Obtener notificaciones del usuario", description = "Devuelve todas las notificaciones no eliminadas del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificaciones", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificacionResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<NotificacionResponseDto>> obtenerMisNotificaciones(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        List<NotificacionResponseDto> notificaciones = notificacionService
                .obtenerNotificacionesUsuario(usuario.getId());
        return ResponseEntity.ok(notificaciones);
    }

    // Crear notificación (solo administradores)
    @Operation(summary = "Crear notificación", description = "Crea una nueva notificación. Solo para administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notificación creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificacionResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos de administrador", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<NotificacionResponseDto> crearNotificacion(
            @Valid @RequestBody NotificacionCreateDto createDto,
            Authentication authentication) {

        Usuario admin = obtenerUsuarioAutenticado(authentication);
        validarPermisoAdmin(admin);

        NotificacionResponseDto notificacion;

        if (createDto.getEntidadId() != null) {
            // Crear notificación completa
            notificacion = notificacionService.crearNotificacionCompleta(
                    createDto.getUsuarioId(),
                    createDto.getTitulo(),
                    createDto.getMensaje(),
                    createDto.getTipo(),
                    createDto.getCategoria(),
                    createDto.getPrioridad(),
                    createDto.getSeveridad(),
                    createDto.getEntidadTipo(),
                    createDto.getEntidadId(),
                    createDto.getDatosAdicionales(),
                    createDto.getFechaExpiracion(),
                    admin);
        } else {
            // Crear notificación básica
            notificacion = notificacionService.crearNotificacion(
                    createDto.getUsuarioId(),
                    createDto.getTitulo(),
                    createDto.getMensaje(),
                    createDto.getTipo(),
                    createDto.getCategoria());
        }

        return ResponseEntity.status(201).body(notificacion);
    }

    // Obtener notificaciones paginadas
    @Operation(summary = "Obtener notificaciones paginadas", description = "Devuelve las notificaciones del usuario con paginación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de notificaciones", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/paginadas")
    public ResponseEntity<Page<NotificacionResponseDto>> obtenerNotificacionesPaginadas(
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            Authentication authentication) {

        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificacionResponseDto> notificaciones = notificacionService
                .obtenerNotificacionesPaginadas(usuario.getId(), pageable);
        return ResponseEntity.ok(notificaciones);
    }

    // Obtener solo notificaciones no leídas
    @Operation(summary = "Obtener notificaciones no leídas", description = "Devuelve solo las notificaciones no leídas del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificaciones no leídas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificacionResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/no-leidas")
    public ResponseEntity<List<NotificacionResponseDto>> obtenerNotificacionesNoLeidas(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        List<NotificacionResponseDto> notificaciones = notificacionService
                .obtenerNotificacionesNoLeidas(usuario.getId());
        return ResponseEntity.ok(notificaciones);
    }

    // Contar notificaciones no leídas
    @Operation(summary = "Contar notificaciones no leídas", description = "Devuelve el número de notificaciones no leídas del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Número de notificaciones no leídas", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/contador")
    public ResponseEntity<Map<String, Long>> contarNotificacionesNoLeidas(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        long cantidad = notificacionService.contarNoLeidas(usuario.getId());
        return ResponseEntity.ok(Map.of("noLeidas", cantidad));
    }

    // Marcar notificación como leída
    @Operation(summary = "Marcar notificación como leída", description = "Marca una notificación específica como leída")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación marcada como leída", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificacionResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PutMapping("/{id}/leer")
    public ResponseEntity<NotificacionResponseDto> marcarComoLeida(
            @Parameter(description = "ID de la notificación") @PathVariable int id,
            Authentication authentication) {

        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        NotificacionResponseDto notificacion = notificacionService.marcarComoLeida(id, usuario.getId());
        return ResponseEntity.ok(notificacion);
    }

    // Marcar todas como leídas
    @Operation(summary = "Marcar todas como leídas", description = "Marca todas las notificaciones no leídas del usuario como leídas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Todas las notificaciones marcadas como leídas"),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PutMapping("/marcar-todas-leidas")
    public ResponseEntity<Map<String, String>> marcarTodasComoLeidas(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        notificacionService.marcarTodasComoLeidas(usuario.getId());
        return ResponseEntity.ok(Map.of("mensaje", "Todas las notificaciones han sido marcadas como leídas"));
    }

    // Archivar notificación
    @Operation(summary = "Archivar notificación", description = "Archiva una notificación específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación archivada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificacionResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PutMapping("/{id}/archivar")
    public ResponseEntity<NotificacionResponseDto> archivarNotificacion(
            @Parameter(description = "ID de la notificación") @PathVariable int id,
            Authentication authentication) {

        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        NotificacionResponseDto notificacion = notificacionService.archivarNotificacion(id, usuario.getId());
        return ResponseEntity.ok(notificacion);
    }

    // Eliminar notificación (soft delete)
    @Operation(summary = "Eliminar notificación", description = "Elimina una notificación específica (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación eliminada"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarNotificacion(
            @Parameter(description = "ID de la notificación") @PathVariable int id,
            Authentication authentication) {

        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        notificacionService.eliminarNotificacion(id, usuario.getId());
        return ResponseEntity.ok(Map.of("mensaje", "Notificación eliminada exitosamente"));
    }

    // Obtener notificaciones críticas
    @Operation(summary = "Obtener notificaciones críticas", description = "Devuelve las notificaciones de alta prioridad del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificaciones críticas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificacionResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/criticas")
    public ResponseEntity<List<NotificacionResponseDto>> obtenerNotificacionesCriticas(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        List<NotificacionResponseDto> notificaciones = notificacionService
                .obtenerNotificacionesCriticas(usuario.getId());
        return ResponseEntity.ok(notificaciones);
    }

    // Obtener notificaciones importantes
    @Operation(summary = "Obtener notificaciones importantes", description = "Devuelve las notificaciones importantes del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificaciones importantes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificacionResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/importantes")
    public ResponseEntity<List<NotificacionResponseDto>> obtenerNotificacionesImportantes(
            Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        List<NotificacionResponseDto> notificaciones = notificacionService
                .obtenerNotificacionesImportantes(usuario.getId());
        return ResponseEntity.ok(notificaciones);
    }

    // Obtener notificaciones recientes
    @Operation(summary = "Obtener notificaciones recientes", description = "Devuelve las notificaciones más recientes del usuario (últimas 24 horas)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificaciones recientes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificacionResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/recientes")
    public ResponseEntity<List<NotificacionResponseDto>> obtenerNotificacionesRecientes(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        List<NotificacionResponseDto> notificaciones = notificacionService
                .obtenerNotificacionesRecientes(usuario.getId());
        return ResponseEntity.ok(notificaciones);
    }

    // Buscar notificaciones por texto
    @Operation(summary = "Buscar notificaciones", description = "Busca notificaciones por texto en título o mensaje")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificaciones encontradas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificacionResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<NotificacionResponseDto>> buscarNotificaciones(
            @Parameter(description = "Texto a buscar en título o mensaje") @RequestParam String q,
            Authentication authentication) {

        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        List<NotificacionResponseDto> notificaciones = notificacionService.buscarNotificaciones(usuario.getId(), q);
        return ResponseEntity.ok(notificaciones);
    }

    // Obtener estadísticas de notificaciones
    @Operation(summary = "Obtener estadísticas de notificaciones", description = "Devuelve estadísticas detalladas de las notificaciones del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas de notificaciones", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Long>> obtenerEstadisticas(Authentication authentication) {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        Map<String, Long> estadisticas = notificacionService.obtenerEstadisticasUsuario(usuario.getId());
        return ResponseEntity.ok(estadisticas);
    }

    // MÉTODOS PRIVADOS - UTILIDADES
    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
        if (usuario == null) {
            throw new AccessDeniedException("Usuario no encontrado");
        }

        return usuario;
    }

    private void validarPermisoAdmin(Usuario usuario) {
        if (usuario.getRol() == null ||
                (!usuario.getRol().name().equals("ADMIN") && !usuario.getRol().name().equals("SUPERADMIN"))) {
            throw new AccessDeniedException("Se requieren permisos de administrador");
        }
    }
}
