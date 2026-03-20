package com.poo.miapi.module.ticket.controller;

import com.poo.miapi.module.ticket.dto.TicketRequestDto;
import com.poo.miapi.module.ticket.dto.TicketResponseDto;
import com.poo.miapi.module.ticket.enums.TicketStatus;
import com.poo.miapi.module.ticket.service.TicketService;
import com.poo.miapi.module.user.model.User;
import com.poo.miapi.module.user.enums.UserRole;
import com.poo.miapi.module.user.service.UserService;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.AccessDeniedException;
import jakarta.validation.Valid;
import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "Endpoints for ticket system management")
public class TicketController {

        private final TicketService ticketService;
        private final UserService userService;

        public TicketController(TicketService ticketService, UserService userService) {
                this.ticketService = ticketService;
                this.userService = userService;
        }

        // GET /api/tickets/v1/all - Admin/SuperAdmin: list all tickets
        @GetMapping("/v1/all")
        @Operation(summary = "List all tickets", description = "Returns all system tickets. Admin/SuperAdmin only.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ticket list", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponseDto.class))),
                        @ApiResponse(responseCode = "403", description = "Not authorized", content = @Content)
        })
        public List<TicketResponseDto> listAllTicketsAdmin(
                        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
                if (user == null || (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.SUPERADMIN)) {
                        throw new AccessDeniedException("Not authorized");
                }
                return ticketService.findAll();
        }

        // GET /api/tickets/v1/support/my-tickets - Support: view their tickets
        @GetMapping("/v1/support/my-tickets")
        @Operation(summary = "List my tickets (Support)", description = "Returns tickets created by the authenticated support user.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ticket list", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponseDto.class))),
                        @ApiResponse(responseCode = "403", description = "Not authorized", content = @Content)
        })
        public List<TicketResponseDto> listMyTicketsSupport(
                        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
                if (user == null || user.getRole() != UserRole.SUPPORT) {
                        throw new AccessDeniedException("Not authorized");
                }
                return ticketService.findByCreator(user.getId());
        }

        // GET /api/tickets/v1/support/tickets-to-evaluate - Support: tickets to
        // evaluate
        @GetMapping("/v1/support/tickets-to-evaluate")
        @Operation(summary = "List tickets to evaluate (Support)", description = "Returns tickets created by the authenticated support user in RESOLVED status.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ticket list", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponseDto.class))),
                        @ApiResponse(responseCode = "403", description = "Not authorized", content = @Content)
        })
        public List<TicketResponseDto> listTicketsToEvaluateSupport(
                        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
                if (user == null || user.getRole() != UserRole.SUPPORT) {
                        throw new AccessDeniedException("Not authorized");
                }
                return ticketService.findByCreatorAndStatus(user.getId(), TicketStatus.RESOLVED);
        }

        // POST /api/tickets/v1/create - Create ticket
        @PostMapping("/v1/create")
        @Operation(summary = "Create ticket", description = "Allows creating a ticket according to the authenticated user's role.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Ticket created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Not authorized", content = @Content)
        })
        public TicketResponseDto createTicket(
                        @Parameter(description = "Ticket data") @RequestBody @Valid TicketRequestDto dto,
                        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
                if (user == null || user.isBlocked()) {
                        throw new IllegalStateException("User blocked or not found");
                }
                return ticketService.createTicketWithRole(dto, user);
        }

        // POST /api/tickets/v1/{id}/reopen - Reopen ticket
        @PostMapping("/v1/{id}/reopen")
        @Operation(summary = "Reopen ticket", description = "Allows reopening a ticket according to the authenticated user's role.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ticket reopened successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponseDto.class))),
                        @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content),
                        @ApiResponse(responseCode = "403", description = "Not authorized", content = @Content),
                        @ApiResponse(responseCode = "400", description = "Ticket cannot be reopened", content = @Content)
        })
        public TicketResponseDto reopenTicket(
                        @Parameter(description = "Ticket ID") @PathVariable int id,
                        @Parameter(description = "Reopen comment") @RequestParam String comment,
                        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
                if (user == null || user.isBlocked()) {
                        throw new AccessDeniedException("User blocked or not found");
                }
                return ticketService.reopenTicket(id, comment, user.getId());
        }

        // GET /api/tickets/v1/developer/available-tickets - Developer: view available tickets
        @GetMapping("/v1/developer/available-tickets")
        @Operation(summary = "List available tickets (Developer)", description = "Returns tickets assigned to the authenticated developer.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ticket list", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TicketResponseDto.class))),
                        @ApiResponse(responseCode = "403", description = "Not authorized", content = @Content)
        })
        public List<TicketResponseDto> listAvailableTicketsDeveloper(
                        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
                if (user == null || user.getRole() != UserRole.DEVELOPER) {
                        throw new AccessDeniedException("Not authorized");
                }
                return ticketService.findByStatus(TicketStatus.PENDING);
        }
}
