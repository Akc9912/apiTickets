package com.poo.miapi.module.users.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.poo.miapi.module.users.api.dto.request.UpdateUserRequest;
import com.poo.miapi.module.users.api.dto.response.UserResponse;
import com.poo.miapi.module.users.enums.UserRole;
import com.poo.miapi.module.users.enums.UserStatus;
import com.poo.miapi.module.users.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Único controller del módulo: perfil propio (/api/user/v1/**) y administración de
 * usuarios (/api/admin/v1/users/**).
 *
 * No expone alta: el registro vive en module/auth (AuthService.register), que es el
 * único que llama a UserService.create().
 *
 * SEGURIDAD — leer antes de agregar un método acá:
 *
 * Al unificar los dos controllers se perdió el @PreAuthorize a nivel de clase (no puede
 * ser el mismo para el perfil propio que para administración), así que **cada método
 * lleva el suyo**. Un método sin anotación queda accesible a cualquier autenticado.
 * Como red de contención, SecurityConfig exige el rol por URL para /api/admin/**.
 *
 * hasAnyRole agrega el prefijo ROLE_, así que esto espera los authorities ROLE_USER,
 * ROLE_ADMIN y ROLE_SUPERADMIN. Hoy nadie los emite todavía (ver nota en SecurityConfig):
 * hasta que se resuelva el principal, todos estos endpoints responden 403.
 *
 * La identidad del autenticado sale de Authentication.getName(), que para cualquier
 * principal basado en UserDetails devuelve el username — acá, el email. Así el controller
 * no depende de cómo se modele el principal, que es una decisión todavía abierta.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Users", description = "Own profile management and user administration")
public class UserController {

    private static final String ANY_ROLE = "hasAnyRole('USER','ADMIN','SUPERADMIN')";
    private static final String ADMIN = "hasAnyRole('ADMIN','SUPERADMIN')";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** PERFIL PROPIO — cualquier usuario autenticado, sobre sus propios datos */

    // GET /api/user/v1/view-profile
    @GetMapping("/user/v1/view-profile")
    @PreAuthorize(ANY_ROLE)
    @Operation(summary = "Get my profile", description = "Retrieves the authenticated user's profile data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully", content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Missing role"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> getMyProfile(
            @Parameter(hidden = true) Authentication authentication) {
        return ResponseEntity.ok(userService.getByEmail(authentication.getName()));
    }

    // PUT /api/user/v1/update-profile
    @PutMapping("/user/v1/update-profile")
    @PreAuthorize(ANY_ROLE)
    @Operation(summary = "Update my profile", description = "Updates the authenticated user's own profile data (first name, last name, phone). Null fields are left untouched.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully", content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Missing role"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> updateMyProfile(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Fields to update; omit or send null to leave a field unchanged") @RequestBody UpdateUserRequest request) {
        // El usuario editado sale del token, nunca del body: así nadie edita el perfil de otro.
        return ResponseEntity.ok(userService.updateOwnProfile(authentication.getName(), request));
    }

    /** ADMINISTRACIÓN — sólo ADMIN y SUPERADMIN */

    // Un solo árbol para ADMIN y SUPERADMIN a propósito: con roles globales en una sola
    // entidad, duplicarlo en /api/superadmin reintroduciría la duplicación por rol que el
    // refactor eliminó. Si una operación tiene que ser exclusiva de SUPERADMIN, se
    // restringe con hasRole('SUPERADMIN') en ese método.

    // GET /api/admin/v1/users[?includeDeleted=true]
    @GetMapping("/admin/v1/users")
    @PreAuthorize(ADMIN)
    @Operation(summary = "List users", description = "Lists active users. With includeDeleted=true also returns soft-deleted ones.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users listed successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not an admin")
    })
    public ResponseEntity<List<UserResponse>> listUsers(
            @Parameter(description = "Include soft-deleted users") @RequestParam(name = "includeDeleted", defaultValue = "false") boolean includeDeleted) {
        return ResponseEntity.ok(includeDeleted
                ? userService.findAllIncludingDeleted()
                : userService.findAllActive());
    }

    // GET /api/admin/v1/users/{id}
    @GetMapping("/admin/v1/users/{id}")
    @PreAuthorize(ADMIN)
    @Operation(summary = "Get user by id", description = "Retrieves a single active user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not an admin"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User id") @PathVariable("id") UUID id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    // GET /api/admin/v1/users/filter/role/{role}
    @GetMapping("/admin/v1/users/filter/role/{role}")
    @PreAuthorize(ADMIN)
    @Operation(summary = "Filter users by role", description = "Lists active users with the given global role. Values: SUPERADMIN, ADMIN, USER.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users listed successfully"),
            @ApiResponse(responseCode = "400", description = "Unknown role"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not an admin")
    })
    public ResponseEntity<List<UserResponse>> filterUsersByRole(
            @Parameter(description = "Global role") @PathVariable("role") UserRole role) {
        return ResponseEntity.ok(userService.findByRole(role));
    }

    // GET /api/admin/v1/users/filter/status/{status}
    @GetMapping("/admin/v1/users/filter/status/{status}")
    @PreAuthorize(ADMIN)
    @Operation(summary = "Filter users by status", description = "Lists active users in the given status. Values: PENDING_VERIFICATION, ACTIVE, INACTIVE, SUSPENDED, DELETED.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users listed successfully"),
            @ApiResponse(responseCode = "400", description = "Unknown status"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not an admin")
    })
    public ResponseEntity<List<UserResponse>> filterUsersByStatus(
            @Parameter(description = "User status") @PathVariable("status") UserStatus status) {
        return ResponseEntity.ok(userService.findByStatus(status));
    }

    // GET /api/admin/v1/users/search?name=
    @GetMapping("/admin/v1/users/search")
    @PreAuthorize(ADMIN)
    @Operation(summary = "Search users by name", description = "Case-insensitive partial match against first and last name. An empty term returns an empty list, not the whole table.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not an admin")
    })
    public ResponseEntity<List<UserResponse>> searchUsers(
            @Parameter(description = "Name fragment") @RequestParam(name = "name", required = false) String name) {
        return ResponseEntity.ok(userService.searchByName(name));
    }

    // PUT /api/admin/v1/users/{id}/status/{status}
    @PutMapping("/admin/v1/users/{id}/status/{status}")
    @PreAuthorize(ADMIN)
    @Operation(summary = "Change user status", description = "Sets the user's status. DELETED is not accepted here: it implies a soft delete, so use DELETE on the user instead.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated", content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Unknown status, or DELETED (use DELETE instead)"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not an admin"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponse> changeUserStatus(
            @Parameter(description = "User id") @PathVariable("id") UUID id,
            @Parameter(description = "New status. DELETED is rejected; use DELETE on the user.") @PathVariable("status") UserStatus status) {
        return ResponseEntity.ok(userService.changeStatus(id, status));
    }

    // DELETE /api/admin/v1/users/{id}
    @DeleteMapping("/admin/v1/users/{id}")
    @PreAuthorize(ADMIN)
    @Operation(summary = "Deactivate user", description = "Soft delete: sets deletedAt and status DELETED. The row is kept, so the email stays taken.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deactivated"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not an admin"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> softDeleteUser(
            @Parameter(description = "User id") @PathVariable("id") UUID id) {
        userService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
