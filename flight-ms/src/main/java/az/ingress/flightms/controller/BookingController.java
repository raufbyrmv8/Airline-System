package az.ingress.flightms.controller;
import az.ingress.flightms.model.dto.response.BookingSearchResponseDto;
import az.ingress.flightms.model.dto.response.FlightResponseDto;
import az.ingress.flightms.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/search")
    public ResponseEntity<List<BookingSearchResponseDto>> search(
            @RequestParam(required = false) String to,
            @RequestParam(required = false) BigDecimal initialPrice,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String date) {
        return ResponseEntity.ok(bookingService.search(to, from, date, initialPrice));
    }
    @GetMapping("/available-seats/{id}")
    public ResponseEntity<FlightResponseDto> availableSeats(@PathVariable(value = "id") Long flightId) {
        return ResponseEntity.ok(bookingService.availableSeats(flightId));
    }
}
