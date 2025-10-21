package az.ingress.flightms.service.kafka;

import az.ingress.common.model.dto.TicketMailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, TicketMailDto> kafkaTemplate;
    public void sendTicketContent(TicketMailDto message) {
        kafkaTemplate.send("ticket-create", message);
    }
}
