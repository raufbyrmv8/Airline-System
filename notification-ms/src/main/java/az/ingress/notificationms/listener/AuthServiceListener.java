package az.ingress.notificationms.listener;

import az.ingress.common.kafka.UserRegisterDto;
import az.ingress.notificationms.service.listener.UserRegisterListenerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceListener {
    private final UserRegisterListenerService userRegisterListenerService;
    @RetryableTopic(
            attempts = "3",
            autoCreateTopics = "true",
            backoff = @Backoff(delay = 1000L, multiplier = 3),
            dltTopicSuffix = "MY_DLT",
            retryTopicSuffix = "MY_RETRY",
            autoStartDltHandler = "true"
    )
    @KafkaListener(topics = "user-registration", groupId = "user-register-notification",containerFactory = "kafkaListenerContainerFactoryForUserRegister")
    public void listenUserRegister(UserRegisterDto dto){
        log.info("User register event received: {}", dto);
        userRegisterListenerService.sendNotification(dto);
    }
    @DltHandler
    public void dltListen(String failedMessage) {
        System.err.println("Failed message from dlt listen: " + failedMessage);
    }
}
