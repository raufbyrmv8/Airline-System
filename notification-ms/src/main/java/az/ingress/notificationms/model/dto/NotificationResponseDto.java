package az.ingress.notificationms.model.dto;

import java.time.LocalDateTime;

public record NotificationResponseDto(Long id, String message, LocalDateTime time){

}
