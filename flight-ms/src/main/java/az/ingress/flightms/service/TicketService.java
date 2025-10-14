package az.ingress.flightms.service;

import az.ingress.flightms.model.dto.request.TicketConfirmationRequestDto;
import az.ingress.flightms.model.dto.request.TicketCreateRequestDto;
import az.ingress.flightms.model.dto.request.TicketCreateResponseDto;
import az.ingress.flightms.model.dto.request.TicketRequestDto;
import az.ingress.flightms.model.dto.response.TicketConfirmationResponseDto;
import az.ingress.flightms.model.dto.response.TicketResponseDto;

public interface TicketService {
    TicketResponseDto createTicketRequest(TicketRequestDto ticketRequestDto);

    TicketCreateResponseDto createTicket(TicketCreateRequestDto ticketCreateRequestDto);

    TicketConfirmationResponseDto confirm(TicketConfirmationRequestDto dto);

    void refundTicket(Long ticketId);
}
