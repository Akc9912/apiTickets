package com.poo.miapi.module.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.AccessDeniedException;

import com.poo.miapi.module.user.dto.UserRequestDto;
import com.poo.miapi.module.user.dto.UserResponseDto;
import com.poo.miapi.module.user.model.User;
import com.poo.miapi.module.user.enums.UserRole;
import com.poo.miapi.module.user.service.UserService;
import com.poo.miapi.module.ticket.dto.ProcessRefundRequestDto;
import com.poo.miapi.module.ticket.dto.TicketRefundRequestResponseDto;
import com.poo.miapi.module.ticket.service.TicketRefundRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Endpoints for administrative system management")
public class AdminController {

    private final UserService userService;
    private final TicketRefundRequestService refundRequestService;

    public AdminController(UserService userService, TicketRefundRequestService refundRequestService) {
        this.userService = userService;
        this.refundRequestService = refundRequestService;
    }

    // POST /api/admin/v1/return-requests/{requestId}/process - Process refund
    // request
    @PostMapping("/v1/return-requests/{requestId}/process")
    @Operation(summary = "Process ticket return request", description = "Approves or rejects a ticket return request from developers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return request processed successfully"),
            @ApiResponse(responseCode = "404", description = "Return request not found"),
            @ApiResponse(responseCode = "403", description = "Not authorized"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<TicketRefundRequestResponseDto> processReturnRequestById(
            @Parameter(description = "Return request ID") @PathVariable int requestId,
            @Parameter(description = "Process request data") @RequestBody @Valid ProcessRefundRequestDto requestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        if (user == null || (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.SUPERADMIN)) {
            throw new AccessDeniedException("Not authorized");
        }
        try {
            TicketRefundRequestResponseDto dto = refundRequestService.processRefundRequest(
                    requestId,
                    requestDto.getAdminId(),
                    requestDto.getApprove(),
                    requestDto.getResolutionComment());
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).build();
        }
    }

    // PUT /api/admin/v1/users/{id}
    @PutMapping("/v1/users/{id}")
    @Operation(summary = "Update user", description = "Updates data of an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDto> updateUser(
            @Parameter(description = "User ID") @PathVariable int id,
            @RequestBody UserRequestDto userDto) {
        UserResponseDto updatedUser = userService.updateUserData(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    // PUT /api/admin/v1/users/{id}/status/toggle-active
    @PutMapping("/v1/users/{id}/status/toggle-active")
    @Operation(summary = "Toggle user active status", description = "Activates or deactivates a user (toggle)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User status updated successfully", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDto> toggleUserActive(
            @Parameter(description = "User ID") @PathVariable int id) {
        UserResponseDto response = userService.toggleUserActive(id);
        return ResponseEntity.ok(response);
    }

    // PUT /api/admin/v1/users/{id}/status/toggle-blocked
    @PutMapping("/v1/users/{id}/status/toggle-blocked")
    @Operation(summary = "Toggle user blocked status", description = "Blocks or unblocks a user (toggle)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User status updated successfully", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDto> toggleUserBlocked(
            @Parameter(description = "User ID") @PathVariable int id) {
        UserResponseDto response = userService.toggleUserBlocked(id);
        return ResponseEntity.ok(response);
    }

    // PUT /api/admin/v1/users/{id}/role
    @PutMapping("/v1/users/{id}/role")
    @Operation(summary = "Update user role", description = "Changes an existing user's role (Admin cannot assign SUPERADMIN role)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User role updated successfully", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Not authorized to assign this role")
    })
    public ResponseEntity<UserResponseDto> updateUserRole(
            @Parameter(description = "User ID") @PathVariable int id,
            @RequestBody UserRequestDto roleDto) {
        UserResponseDto response = userService.updateUserRole(id, roleDto);
        return ResponseEntity.ok(response);
    }

    // GET /api/admin/v1/users - Listar todos los usuarios
    @GetMapping("/v1/users")
    @Operation(summary = "List all users", description = "Returns the list of all system users (Admin cannot see SuperAdmins)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User list retrieved successfully")
    })
    public ResponseEntity<List<UserResponseDto>> listUsers() {
        List<UserResponseDto> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    // GET /api/admin/v1/users/{id} - Obtener usuario por ID
    @GetMapping("/v1/users/{id}")
    @Operation(summary = "Get user details", description = "Retrieves data of a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User data retrieved successfully", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDto> getUser(
            @Parameter(description = "User ID") @PathVariable int id) {
        UserResponseDto user = userService.getDetails(id);
        return ResponseEntity.ok(user);
    }

    // GET /api/admin/v1/return-requests - List refund requests
    @GetMapping("/v1/return-requests")
    @Operation(summary = "List return requests", description = "Retrieves all pending ticket return requests from developers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return requests retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    public ResponseEntity<List<TicketRefundRequestResponseDto>> listReturnRequests(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        if (user == null || (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.SUPERADMIN)) {
            throw new AccessDeniedException("Not authorized");
        }
        List<TicketRefundRequestResponseDto> requests = refundRequestService.findPendingRequests();
        return ResponseEntity.ok(requests);
    }
}
