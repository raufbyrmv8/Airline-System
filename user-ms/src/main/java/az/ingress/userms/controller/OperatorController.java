package az.ingress.userms.controller;

import az.ingress.userms.model.dto.request.OperatorRequestDto;
import az.ingress.userms.service.OperatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/operators")
public class OperatorController {
    private final OperatorService operatorService;
    @PutMapping("/register")
    public ResponseEntity<Void> register(@RequestBody OperatorRequestDto dto) {
        operatorService.registerOperator(dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/admin/operator/approval")
    public ResponseEntity<Void> approvalOperator(@RequestBody OperatorRequestDto dto) {
        operatorService.approvalOperator(dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/admin/operator/remove")
    public ResponseEntity<Void> removeOperator(@RequestBody OperatorRequestDto dto) {
        operatorService.removeOperatorRole(dto);
        return ResponseEntity.ok().build();
    }
}
