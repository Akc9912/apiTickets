package com.poo.miapi.module.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.poo.miapi.module.user.dto.UserRequestDto;
import com.poo.miapi.module.user.dto.UserResponseDto;
import com.poo.miapi.module.user.service.SuperadminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/superadmin")
@Tag(name = "SuperAdmin", description = "Exclusive SuperAdmin endpoints - System owner with full access")
@SecurityRequirement(name = "bearerAuth")
public class SuperadminController {
        private final SuperadminService superadminService;

        public SuperadminController(SuperadminService superadminService) {
                this.superadminService = superadminService;
        }

        // POST /api/superadmin/v1/users - Crear usuario
        @PostMapping("/v1/users")
        @Operation(summary = "Create user", description = "Creates a new user with any role (SuperAdmin, Admin, Developer, Support)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid data or duplicate email")
        })
        public ResponseEntity<UserResponseDto> createUser(
                        @Parameter(description = "New user data") @RequestBody UserRequestDto userDto) {
                UserResponseDto created = superadminService.createUser(userDto);
                return ResponseEntity.status(201).body(created);
        }

        // GET /api/superadmin/v1/users - Listar todos los usuarios
        @GetMapping("/v1/users")
        @Operation(summary = "List all users", description = "Retrieves the complete list of system users")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User list retrieved successfully")
        })
        public ResponseEntity<List<UserResponseDto>> listAllUsers() {
                List<UserResponseDto> users = superadminService.findAllUsers();
                return ResponseEntity.ok(users);
        }

        // GET /api/superadmin/v1/users/filter/role/{role} - Listar por rol
        @GetMapping("/v1/users/filter/role/{role}")
        @Operation(summary = "List users by role", description = "Retrieves users filtered by role (ADMIN, DEVELOPER, SUPPORT, SUPERADMIN)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User list retrieved successfully")
        })
        public ResponseEntity<List<UserResponseDto>> listUsersByRole(
                        @Parameter(description = "Role to filter") @PathVariable String role) {
                List<UserResponseDto> users = superadminService.findUsersByRole(role);
                return ResponseEntity.ok(users);
        }

        // GET /api/superadmin/v1/users/filter/status/active - Listar activos
        @GetMapping("/v1/users/filter/status/active")
        @Operation(summary = "List active users", description = "Retrieves all users with active status")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Active users retrieved successfully")
        })
        public ResponseEntity<List<UserResponseDto>> listActiveUsers() {
                List<UserResponseDto> users = superadminService.findActiveUsers();
                return ResponseEntity.ok(users);
        }

        // GET /api/superadmin/v1/users/filter/status/blocked - Listar bloqueados
        @GetMapping("/v1/users/filter/status/blocked")
        @Operation(summary = "List blocked users", description = "Retrieves all users with blocked status")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Blocked users retrieved successfully")
        })
        public ResponseEntity<List<UserResponseDto>> listBlockedUsers() {
                List<UserResponseDto> users = superadminService.findBlockedUsers();
                return ResponseEntity.ok(users);
        }

        // PUT /api/superadmin/v1/users/{id}/status/activate - Activar usuario
        @PutMapping("/v1/users/{id}/status/activate")
        @Operation(summary = "Activate user", description = "Activates a specific user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User activated successfully"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        public ResponseEntity<UserResponseDto> activateUser(
                        @Parameter(description = "User ID") @PathVariable int id) {
                UserResponseDto user = superadminService.activateUser(id);
                return ResponseEntity.ok(user);
        }

        // PUT /api/superadmin/v1/users/{id}/status/deactivate - Desactivar usuario
        @PutMapping("/v1/users/{id}/status/deactivate")
        @Operation(summary = "Deactivate user", description = "Deactivates a specific user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User deactivated successfully"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        public ResponseEntity<UserResponseDto> deactivateUser(
                        @Parameter(description = "User ID") @PathVariable int id) {
                UserResponseDto user = superadminService.deactivateUser(id);
                return ResponseEntity.ok(user);
        }

        // DELETE /api/superadmin/v1/users/{id} - Eliminar usuario
        @DeleteMapping("/v1/users/{id}")
        @Operation(summary = "Delete user", description = "Permanently deletes a user from the system")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        public ResponseEntity<Void> deleteUser(
                        @Parameter(description = "User ID") @PathVariable int id) {
                superadminService.deleteUser(id);
                return ResponseEntity.noContent().build();
        }
}
