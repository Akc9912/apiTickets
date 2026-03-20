package com.poo.miapi.module.user.controller;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import com.poo.miapi.module.user.enums.UserRole;
import com.poo.miapi.module.user.model.User;
import com.poo.miapi.module.user.service.DeveloperService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/developer")
@Tag(name = "Developer", description = "Endpoints for developer ticket operations")
public class DeveloperController {

        private final DeveloperService developerService;

        public DeveloperController(DeveloperService developerService) {
                this.developerService = developerService;
        }

        // POST /api/developer/v1/tickets/{ticketId}/take - Tomar ticket
        @PostMapping("/v1/tickets/{ticketId}/take")
        @Operation(summary = "Take ticket", description = "Allows a developer to take pending or reopened tickets")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ticket taken successfully"),
                        @ApiResponse(responseCode = "404", description = "Developer or ticket not found"),
                        @ApiResponse(responseCode = "400", description = "Ticket is already assigned or not available")
        })
        public ResponseEntity<String> takeTicket(
                        @Parameter(description = "Developer ID") @RequestParam int developerId,
                        @Parameter(description = "Ticket ID") @PathVariable int ticketId) {
                try {
                        developerService.takeTicket(developerId, ticketId);
                        return ResponseEntity.ok("Ticket taken successfully");
                } catch (EntityNotFoundException e) {
                        return ResponseEntity.status(404).body("Developer or ticket not found");
                } catch (UnsupportedOperationException e) {
                        return ResponseEntity.status(501).body(e.getMessage());
                } catch (IllegalStateException | IllegalArgumentException e) {
                        return ResponseEntity.status(400).body(e.getMessage());
                } catch (Exception e) {
                        return ResponseEntity.status(500).body("Internal server error");
                }
        }

        // POST /api/developer/v1/tickets/{ticketId}/resolve - Resolver ticket
        @PostMapping("/v1/tickets/{ticketId}/resolve")
        @Operation(summary = "Resolve ticket", description = "Marks a ticket as resolved by the developer")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ticket resolved successfully"),
                        @ApiResponse(responseCode = "404", description = "Developer or ticket not found"),
                        @ApiResponse(responseCode = "400", description = "Ticket is not assigned to this developer")
        })
        public ResponseEntity<String> resolveTicket(
                        @Parameter(description = "Developer ID") @RequestParam int developerId,
                        @Parameter(description = "Ticket ID") @PathVariable int ticketId,
                        @Parameter(description = "Resolution comment") @RequestParam(required = false) String comment) {
                try {
                        developerService.resolveTicket(developerId, ticketId, comment);
                        return ResponseEntity.ok("Ticket status updated to: Resolved");
                } catch (EntityNotFoundException e) {
                        return ResponseEntity.status(404).body("Ticket not found");
                } catch (UnsupportedOperationException e) {
                        return ResponseEntity.status(501).body(e.getMessage());
                } catch (IllegalStateException | IllegalArgumentException e) {
                        return ResponseEntity.status(400).body(e.getMessage());
                } catch (Exception e) {
                        return ResponseEntity.status(500).body("Internal server error");
                }
        }

        // POST /api/developer/v1/tickets/{ticketId}/return - Devolver ticket
        @PostMapping("/v1/tickets/{ticketId}/return")
        @Operation(summary = "Return ticket", description = "Returns a ticket with a specific reason")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ticket return request submitted successfully"),
                        @ApiResponse(responseCode = "404", description = "Developer or ticket not found"),
                        @ApiResponse(responseCode = "400", description = "Ticket is not assigned to this developer")
        })
        public ResponseEntity<String> returnTicket(
                        @Parameter(description = "Developer ID") @RequestParam int developerId,
                        @Parameter(description = "Ticket ID") @PathVariable int ticketId,
                        @Parameter(description = "Reason for ticket return") @RequestParam String reason) {
                try {
                        developerService.requestTicketReturn(developerId, ticketId, reason);
                        return ResponseEntity.ok("Return request submitted successfully");
                } catch (EntityNotFoundException e) {
                        return ResponseEntity.status(404).body("Developer or ticket not found");
                } catch (UnsupportedOperationException e) {
                        return ResponseEntity.status(501).body(e.getMessage());
                } catch (IllegalStateException | IllegalArgumentException e) {
                        return ResponseEntity.status(400).body(e.getMessage());
                } catch (Exception e) {
                        return ResponseEntity.status(500).body("Internal server error");
                }
        }
}
