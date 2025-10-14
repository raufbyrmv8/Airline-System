package az.ingress.flightms.controller;
import az.ingress.flightms.model.dto.FlightDto;
import az.ingress.flightms.model.dto.FlightDtoByCreatedOperator;
import az.ingress.flightms.model.dto.request.FlightRequestDto;
import az.ingress.flightms.service.BookingService;
import az.ingress.flightms.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/flights")
public class FlightController {

    private final FlightService flightService;
    private final BookingService bookingService;


    @GetMapping("/{id}")
    public ResponseEntity<FlightDto> getById(@PathVariable Long id) {
        FlightDto dto = flightService.getById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<FlightDto>> getAll() {
        List<FlightDto> dtoList = flightService.getAll();
        return ResponseEntity.ok(dtoList);
    }
    @GetMapping("/pending-approval")
    public ResponseEntity<List<FlightDtoByCreatedOperator>> getFlightsByStateWithOperatorDetails() {
        List<FlightDtoByCreatedOperator> dtoList = flightService.getPendingFlightsWithOperatorDetails();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("approval-state/{state}")
    public ResponseEntity<List<FlightDto>> getFlightsByState(@PathVariable String state) {
        List<FlightDto> dtoList = flightService.getFlightsByState(state);
        return ResponseEntity.ok(dtoList);
    }
    @PostMapping
    public ResponseEntity<FlightDto> create(@RequestBody @Validated FlightRequestDto dto) {
        FlightDto createdDto = flightService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightDto> update(@PathVariable Long id, @RequestBody @Validated FlightRequestDto dto) {
        FlightDto updatedDto = flightService.update(id, dto);
        return ResponseEntity.ok().body((updatedDto));
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<String> reject(@PathVariable Long id, @RequestParam(required = false) String feedback) {
        flightService.rejectFlight(id, feedback);
        return ResponseEntity.ok().body("Flight with id " + id + " rejected" + "Email sent to operator");
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<String> approve(@PathVariable Long id, @RequestParam(required = false) String feedback) {
        flightService.approveFlight(id, feedback);
        return ResponseEntity.ok().body("Flight with id " + id + " approved" + "Email sent to operator");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        flightService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/flight/cancel/{id}")
    public ResponseEntity<Void> cancelFlight(@PathVariable(value = "id") Long flightId) {
        bookingService.cancelFlight(flightId);
        return ResponseEntity.ok().build();
    }

}
