package az.ingress.flightms.controller;
import az.ingress.flightms.model.dto.request.TicketConfirmationRequestDto;
import az.ingress.flightms.model.dto.request.TicketCreateRequestDto;
import az.ingress.flightms.model.dto.request.TicketCreateResponseDto;
import az.ingress.flightms.model.dto.request.TicketRequestDto;
import az.ingress.flightms.model.dto.response.TicketConfirmationResponseDto;
import az.ingress.flightms.model.dto.response.TicketResponseDto;
import az.ingress.flightms.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @PostMapping("/request")
    public ResponseEntity<TicketResponseDto> createTicketRequest(@RequestBody TicketRequestDto ticketRequestDto) {
        return ResponseEntity.ok(ticketService.createTicketRequest(ticketRequestDto));
    }

    @PostMapping("/create")
    public ResponseEntity<TicketCreateResponseDto> createResponseDtoResponseEntity(@RequestBody @Valid TicketCreateRequestDto ticketCreateRequestDto) {
        return ResponseEntity.ok(ticketService.createTicket(ticketCreateRequestDto));
    }
    @PostMapping("/confirm")
    public ResponseEntity<TicketConfirmationResponseDto> confirm(@RequestBody TicketConfirmationRequestDto dto){
        return ResponseEntity.ok(ticketService.confirm(dto));
    }
    @GetMapping("refund/{ticketId}")
    public ResponseEntity<Void> refundTicket(@PathVariable Long ticketId){
        ticketService.refundTicket(ticketId);
        return ResponseEntity.ok().build();
    }
}
