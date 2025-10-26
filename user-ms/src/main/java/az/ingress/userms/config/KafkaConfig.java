package az.ingress.userms.config;
import az.ingress.common.kafka.OperatorRegisterDto;
import az.ingress.common.kafka.UserRegisterDto;
import az.ingress.common.kafka.UserResetPasswordDto;
import com.fasterxml.jackson.databind.JsonSerializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;


import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, UserRegisterDto> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    @Bean
    public ProducerFactory<String, UserResetPasswordDto> passwordProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    @Bean
    public ProducerFactory<String, OperatorRegisterDto> operatorProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }


    @Bean
    public KafkaTemplate<String, UserRegisterDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    @Bean
    public KafkaTemplate<String, UserResetPasswordDto> passwordKafkaTemplate() {
        return new KafkaTemplate<>(passwordProducerFactory());
    }
    @Bean
    public KafkaTemplate<String, OperatorRegisterDto> operatorKafkaTemplate() {
        return new KafkaTemplate<>(operatorProducerFactory());
    }


    @Bean
    public NewTopic topicOrders() {
        return new NewTopic("user-registration", 1, (short) 1);
    }
    @Bean
    public NewTopic forgetPassword() {
        return new NewTopic("reset-password", 4, (short) 1);
    }
    @Bean
    public NewTopic operator() {
        return new NewTopic("operator-register", 4, (short) 1);
    }
}
