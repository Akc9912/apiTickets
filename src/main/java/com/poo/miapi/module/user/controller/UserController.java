package com.poo.miapi.module.user.controller;

import com.poo.miapi.module.user.dto.UserRequestDto;
import com.poo.miapi.module.user.dto.UserResponseDto;
import com.poo.miapi.module.user.model.User;
import com.poo.miapi.module.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Profile", description = "Endpoints for authenticated user's own profile management")
public class UserController {

        private final UserService userService;

        public UserController(UserService userService) {
                this.userService = userService;
        }

        // GET /api/user/v1/profile
        @GetMapping("/v1/profile")
        @Operation(summary = "Get my profile", description = "Retrieves the authenticated user's profile data")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User profile retrieved successfully", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
                        @ApiResponse(responseCode = "401", description = "Not authenticated")
        })
        public ResponseEntity<UserResponseDto> getMyProfile(
                        @Parameter(hidden = true) @AuthenticationPrincipal User authenticatedUser) {
                if (authenticatedUser == null) {
                        return ResponseEntity.status(401).build();
                }
                UserResponseDto user = userService.getDetails(authenticatedUser.getId());
                return ResponseEntity.ok(user);
        }

        // PUT /api/user/v1/profile
        @PutMapping("/v1/profile")
        @Operation(summary = "Update my profile", description = "Updates the authenticated user's profile data")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Profile updated successfully", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
                        @ApiResponse(responseCode = "401", description = "Not authenticated"),
                        @ApiResponse(responseCode = "400", description = "Invalid data")
        })
        public ResponseEntity<UserResponseDto> updateMyProfile(
                        @Parameter(hidden = true) @AuthenticationPrincipal User authenticatedUser,
                        @Parameter(description = "New user data") @RequestBody UserRequestDto userDto) {
                if (authenticatedUser == null) {
                        return ResponseEntity.status(401).build();
                }
                UserResponseDto updatedUser = userService.updateUserData(authenticatedUser.getId(), userDto);
                return ResponseEntity.ok(updatedUser);
        }

}