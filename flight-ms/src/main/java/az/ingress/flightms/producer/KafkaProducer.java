package az.ingress.flightms.producer;
import az.ingress.common.kafka.AdminNotificationDto;
import az.ingress.common.kafka.OperatorNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, AdminNotificationDto> adminNotificationTemplate;
    private final KafkaTemplate<String, OperatorNotificationDto> operatorNotificationTemplate;

    public void notifyAdminForApprovement(String topic, AdminNotificationDto createdFlightId) {
        adminNotificationTemplate.send(topic, createdFlightId);
        log.info("Flight {} sent for admin for approval", createdFlightId);
    }

    public void notifyOperator(String topic, OperatorNotificationDto notification) {
        operatorNotificationTemplate.send(topic, notification);
        log.info("Notification sent to operator {}", notification);
    }

}
