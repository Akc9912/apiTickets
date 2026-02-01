package com.poo.miapi.module.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.poo.miapi.module.auth.service.AuthService;
import com.poo.miapi.module.auth.dto.LoginRequestDto;
import com.poo.miapi.module.auth.dto.LoginResponseDto;
import com.poo.miapi.module.auth.dto.ChangePasswordDto;
import com.poo.miapi.module.auth.dto.ResetPasswordDto;
import com.poo.miapi.module.user.enums.UserRole;
import com.poo.miapi.module.user.model.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.Map;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for authentication and password management")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/login
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login", content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<?> login(
            @Parameter(description = "Login credentials") @RequestBody @Valid LoginRequestDto request) {
        try {
            LoginResponseDto response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }

    // POST /api/auth/change-password
    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Allows user to change their current password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data or incorrect current password"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<String> changePassword(
            @Parameter(description = "Password change data") @RequestBody @Valid ChangePasswordDto dto,
            @Parameter(hidden = true) @AuthenticationPrincipal User authenticatedUser) {
        try {
            authService.changePassword(dto);
            return ResponseEntity.ok("Password updated successfully");
        } catch (Exception ex) {
            throw ex;
        }
    }

    // POST /api/auth/reset-password
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Resets a user's password (administrative functionality)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "403", description = "Not authorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<String> resetPassword(
            @Parameter(description = "Password reset data") @RequestBody @Valid ResetPasswordDto dto,
            @Parameter(hidden = true) @AuthenticationPrincipal User authenticatedUser) {
        if (authenticatedUser == null || authenticatedUser.getRole() == null) {
            return ResponseEntity.status(403).body("Not authorized");
        }

        UserRole role = authenticatedUser.getRole();
        if (role != UserRole.ADMIN && role != UserRole.SUPERADMIN) {
            return ResponseEntity.status(403).body("Only admin or superadmin can reset passwords");
        }

        try {
            authService.resetPassword(dto);
            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception ex) {
            throw ex;
        }
    }
}
