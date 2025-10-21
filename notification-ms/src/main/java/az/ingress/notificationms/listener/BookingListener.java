package az.ingress.notificationms.listener;
import az.ingress.common.model.dto.TicketMailDto;
import az.ingress.notificationms.service.listener.impl.TicketCreateService;
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
public class BookingListener {
    private final TicketCreateService ticketCreateService;

    @RetryableTopic(
            attempts = "3",
            autoCreateTopics = "true",
            backoff = @Backoff(delay = 1000L, multiplier = 3),
            dltTopicSuffix = "MY_DLT",
            retryTopicSuffix = "MY_RETRY",
            autoStartDltHandler = "true"
    )
    @KafkaListener(topics = "ticket-create",
            groupId = "ticket-create-group",
            containerFactory = "kafkaListenerContainerFactoryForTicketCreate")
    public void listenUserRegister(TicketMailDto dto) {
        log.info("User register event received: {}", dto);
        ticketCreateService.sendNotification(dto);
    }

    @DltHandler
    public void dltListen(String failedMessage) {
        System.err.println("Failed message in ticket-create from dlt listen: " + failedMessage);
    }
}
