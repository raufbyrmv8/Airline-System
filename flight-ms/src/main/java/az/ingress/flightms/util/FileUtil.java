package az.ingress.flightms.util;

import az.ingress.flightms.model.entity.FlightPlanePlace;
import az.ingress.flightms.model.entity.Ticket;

import java.math.BigDecimal;

public class FileUtil {
    public static String createTicketExample(Ticket ticket, FlightPlanePlace flightPlanePlace) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ticket No: ").append(ticket.getTicketNo()).append("\n");
        sb.append("Passenger Name: ").append(ticket.getPassengerName()).append("\n");
        sb.append("Passenger Surname: ").append(ticket.getPassengerSurname()).append("\n");
        sb.append("Email: ").append(ticket.getEmail()).append("\n");
        sb.append("Phone: ").append(ticket.getPhone()).append("\n");
        sb.append("From: ").append(ticket.getFlight().getFrom().getCity()).append("\n");
        sb.append("To: ").append(ticket.getFlight().getTo().getCity()).append("\n");
        sb.append("Departure Time: ").append(ticket.getFlight().getDepartureTime()).append("\n");
        sb.append("Arrival Time: ").append(ticket.getFlight().getArrivalTime()).append("\n");
        sb.append("Price: ").append(ticket.getFlight().getPrice()).append("\n");
        sb.append("Plane Place: ").append(flightPlanePlace.getPlanePlace().getPlaceNumber()).append("\n");
        return sb.toString();
    }

    public static String createRefundedTicketContent(Ticket ticket) {
        StringBuilder sb = new StringBuilder();
        sb.append("Your ticket has been refunded.\n");
        sb.append("Refunded Ticket\n");
        sb.append("Ticket No: ").append(ticket.getTicketNo()).append("\n");
        sb.append("Passenger Name: ").append(ticket.getPassengerName()).append("\n");
        sb.append("Passenger Surname: ").append(ticket.getPassengerSurname()).append("\n");
        sb.append("Email: ").append(ticket.getEmail()).append("\n");
        sb.append("Phone: ").append(ticket.getPhone()).append("\n");
        sb.append("From: ").append(ticket.getFlight().getFrom().getCity()).append("\n");
        sb.append("To: ").append(ticket.getFlight().getTo().getCity()).append("\n");
        sb.append("Departure Time: ").append(ticket.getFlight().getDepartureTime()).append("\n");
        sb.append("Arrival Time: ").append(ticket.getFlight().getArrivalTime()).append("\n");
        sb.append("Price: ").append(ticket.getFlight().getPrice()).append("\n");
        sb.append("Refunded Price: ").append(ticket.getFlight().getPrice().divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(50))).append("\n");
        return sb.toString();
    }
    public static String createCancelFlightContent(Ticket ticket) {
        StringBuilder sb = new StringBuilder();
        sb.append("Flight has been canceled.\n");
        sb.append("We are sorry to inform you that your flight has been canceled.\n");
        sb.append("your money will be refunded.\n");
        sb.append("Refunded Ticket\n");
        sb.append("Ticket No: ").append(ticket.getTicketNo()).append("\n");
        sb.append("Passenger Name: ").append(ticket.getPassengerName()).append("\n");
        sb.append("Passenger Surname: ").append(ticket.getPassengerSurname()).append("\n");
        sb.append("Email: ").append(ticket.getEmail()).append("\n");
        sb.append("Phone: ").append(ticket.getPhone()).append("\n");
        sb.append("From: ").append(ticket.getFlight().getFrom().getCity()).append("\n");
        sb.append("To: ").append(ticket.getFlight().getTo().getCity()).append("\n");
        sb.append("Departure Time: ").append(ticket.getFlight().getDepartureTime()).append("\n");
        sb.append("Arrival Time: ").append(ticket.getFlight().getArrivalTime()).append("\n");
        sb.append("Price: ").append(ticket.getFlight().getPrice()).append("\n");
        sb.append("Refunded Price: ").append(ticket.getFlight().getPrice().divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(50))).append("\n");
        return sb.toString();
    }
}
