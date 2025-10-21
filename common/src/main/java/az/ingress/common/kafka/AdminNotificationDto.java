package az.ingress.common.kafka;

public record AdminNotificationDto(
        Long operatorId,
        Long flightId,
        String subject) {
}
