package az.ingress.flightms.util;

import az.ingress.flightms.model.dto.response.BookingSearchResponseDto;
import az.ingress.flightms.model.entity.Flight;

import java.util.List;

public class MapperUtil {
    public static void mapDto(List<Flight> flights, List<BookingSearchResponseDto> res) {
        flights.forEach(flight -> {
            BookingSearchResponseDto bookingSearchResponseDto = new BookingSearchResponseDto();
            bookingSearchResponseDto.setFlightId(flight.getId());
            bookingSearchResponseDto.setFrom((flight.getFrom() + " - " + flight.getFrom().getCity() + ", " + flight.getFrom().getCountry()));
            bookingSearchResponseDto.setTo((flight.getTo() + " - " + flight.getTo().getCity() + ", " + flight.getTo().getCountry()));
            bookingSearchResponseDto.setDate(flight.getDepartureTime());
            bookingSearchResponseDto.setPrice(flight.getPrice());
            res.add(bookingSearchResponseDto);
        });
    }


}
