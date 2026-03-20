package com.poo.miapi.module.user.controller;

import com.poo.miapi.module.ticket.dto.TicketResponseDto;
import com.poo.miapi.module.user.model.User;
import com.poo.miapi.module.user.enums.UserRole;
import com.poo.miapi.module.user.service.SupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support")
@Tag(name = "Support", description = "Endpoints for support ticket management and creation")
public class SupportController {

    private final SupportService supportService;

    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    // POST /api/support/v1/tickets/{ticketId}/evaluate - Validación final del
    // support
    @PostMapping("/v1/tickets/{ticketId}/evaluate")
    @Operation(summary = "Evaluate ticket solution", description = "Final support validation: accepts the solution (CLOSED) or rejects it (REOPENED)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket evaluated successfully", content = @Content(schema = @Schema(implementation = TicketResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Ticket not found"),
            @ApiResponse(responseCode = "403", description = "Not authorized"),
            @ApiResponse(responseCode = "400", description = "Ticket is not in RESOLVED state or cannot be evaluated")
    })
    public TicketResponseDto evaluateTicket(
            @Parameter(description = "Ticket ID") @PathVariable int ticketId,
            @Parameter(description = "Approve solution?") @RequestParam boolean approve,
            @Parameter(description = "Evaluation comment") @RequestParam(required = false) String comment,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        if (user == null || user.getRole() != UserRole.SUPPORT) {
            throw new AccessDeniedException("Not authorized");
        }
        return supportService.evaluateTicket(ticketId, user.getId(), approve, comment);
    }

}
