package az.ingress.flightms.config;

import az.ingress.common.kafka.AdminNotificationDto;
import az.ingress.common.kafka.OperatorNotificationDto;
import az.ingress.common.model.dto.TicketMailDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    private Map<String, Object> producerConfigProps() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return configProps;
    }


    @Bean
    public ProducerFactory<String, TicketMailDto> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ProducerFactory<String, AdminNotificationDto> adminProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigProps());
    }

    @Bean
    public ProducerFactory<String, OperatorNotificationDto> operatorProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigProps());
    }

    @Bean
    public KafkaTemplate<String, AdminNotificationDto> adminNotificationTemplate() {
        return new KafkaTemplate<>(adminProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, TicketMailDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic topicOrders() {
        return new NewTopic("admin-notification", 1, (short) 1);
    }

    @Bean
    public KafkaTemplate<String, OperatorNotificationDto> operatorNotificationTemplate() {
        return new KafkaTemplate<>(operatorProducerFactory());
    }

    @Bean
    public NewTopic operatorNotificationTopic() {
        return new NewTopic("operator-notification", 1, (short) 1);
    }

    @Bean
    public NewTopic createTicketTopic() {
        return new NewTopic("ticket-create", 1, (short) 1);
    }

}
