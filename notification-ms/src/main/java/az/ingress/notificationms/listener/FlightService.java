package az.ingress.notificationms.listener;
import az.ingress.common.kafka.AdminNotificationDto;
import az.ingress.common.kafka.OperatorNotificationDto;
import az.ingress.notificationms.service.listener.AdminEmailService;
import az.ingress.notificationms.service.listener.OperatorEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightService {
    private final AdminEmailService adminEmailService;
    private final OperatorEmailService operatorEmailService;

    @RetryableTopic(
            attempts = "3",
            autoCreateTopics = "true",
            backoff = @Backoff(delay = 1000L, multiplier = 3),
            dltTopicSuffix = "MY_DLT",
            retryTopicSuffix = "MY_RETRY",
            autoStartDltHandler = "true"
    )
    @KafkaListener(topics = "admin-notification", groupId = "admin-notification-group", containerFactory = "kafkaListenerContainerFactoryForAdminNotification")
    public void listenAdminApproval(AdminNotificationDto dto) {
        log.info("Admin notification event received: {}", dto);
        adminEmailService.sendNotification(dto);
    }

    @RetryableTopic(
            attempts = "3",
            autoCreateTopics = "true",
            backoff = @Backoff(delay = 1000L, multiplier = 3),
            dltTopicSuffix = "MY_DLT",
            retryTopicSuffix = "MY_RETRY",
            autoStartDltHandler = "true"
    )
    @KafkaListener(topics = "operator-notification", groupId = "operator-notification-group", containerFactory = "kafkaListenerContainerFactoryForOperatorNotification")
    public void listenOperatorInform(OperatorNotificationDto dto) {
        log.info("Operator notification event received: {}", dto);
        operatorEmailService.sendNotification(dto);
    }


}