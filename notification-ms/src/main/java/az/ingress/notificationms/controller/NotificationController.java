package az.ingress.notificationms.controller;
import az.ingress.notificationms.model.dto.NotificationResponseDto;
import az.ingress.notificationms.service.listener.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/by-state")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByState(@RequestParam(defaultValue = "UNREAD") String state) {
        List<NotificationResponseDto> notifications = notificationService.getNotificationsByState(state);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/mark-as-read")
    public ResponseEntity<Void> markAsRead(@RequestBody List<Long> ids) {
        notificationService.markAsRead(ids);
        return ResponseEntity.ok().build();
    }

}
