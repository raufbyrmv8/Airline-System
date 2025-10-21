package az.ingress.flightms.controller;
import az.ingress.flightms.model.dto.request.PlanePlaceRequest;
import az.ingress.flightms.model.dto.response.PlanePlaceResponse;
import az.ingress.flightms.service.PlanePlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/plane-place")
@RequiredArgsConstructor
public class PlanePlaceController {
    private final PlanePlaceService planePlaceService;

    @PostMapping
    public ResponseEntity<PlanePlaceResponse> create(@RequestBody PlanePlaceRequest planePlaceRequest) {
       PlanePlaceResponse planePlaceResponse= planePlaceService.createPlanePlace(planePlaceRequest);
       return ResponseEntity.status(HttpStatus.CREATED).body(planePlaceResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanePlaceResponse> getById(@PathVariable("id") Long id) {
        PlanePlaceResponse planePlaceResponse= planePlaceService.getPlanePlaceById(id);
        return ResponseEntity.ok(planePlaceResponse);
    }

    @GetMapping
    public ResponseEntity<Set<PlanePlaceResponse>> getAll() {
       Set<PlanePlaceResponse> planePlaceResponses=planePlaceService.getAllPlanePlaces();
        return ResponseEntity.ok(planePlaceResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanePlaceResponse> update(@PathVariable Long id,@RequestBody PlanePlaceRequest planePlaceRequest) {
       PlanePlaceResponse planePlaceResponse= planePlaceService.updatePlanePlace(id, planePlaceRequest);
         return ResponseEntity.ok(planePlaceResponse);
    }
}
