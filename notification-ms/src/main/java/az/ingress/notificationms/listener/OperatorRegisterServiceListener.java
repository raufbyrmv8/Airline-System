package az.ingress.notificationms.listener;
import az.ingress.common.kafka.OperatorRegisterDto;
import az.ingress.notificationms.service.listener.impl.OperatorRegisterListenerServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperatorRegisterServiceListener {

    private final OperatorRegisterListenerServiceImpl operatorRegisterListenerService;

    @RetryableTopic(
            attempts = "3",
            autoCreateTopics = "true",
            backoff = @Backoff(delay = 1000L, multiplier = 3),
            dltTopicSuffix = "MY_DLT",
            retryTopicSuffix = "MY_RETRY",
            autoStartDltHandler = "true"
    )
    @KafkaListener(topics = "operator-register", groupId = "operator-register-notification", containerFactory = "kafkaListenerContainerFactoryForOperatorRegister")
    public void listen(OperatorRegisterDto operatorRegisterDto) {

        operatorRegisterListenerService.sendNotificationApp(operatorRegisterDto);
        operatorRegisterListenerService.sendNotificationEmail(operatorRegisterDto);
    }
}
