package az.ingress.userms.producer;
import az.ingress.common.kafka.OperatorRegisterDto;
import az.ingress.common.kafka.UserRegisterDto;
import az.ingress.common.kafka.UserResetPasswordDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, UserRegisterDto> kafkaTemplate;
    private final KafkaTemplate<String, UserResetPasswordDto> userResetPasswordDtoKafkaTemplate;
    private final KafkaTemplate<String, OperatorRegisterDto> operatorRegisterDtoKafkaTemplate;

    public void sendUserRegistration(String topic, UserRegisterDto userRegisterDto) {
        kafkaTemplate.send(topic, userRegisterDto);
    }

    public void sendPasswordResetMessage(String topic, UserResetPasswordDto userResetPasswordDto) {
        userResetPasswordDtoKafkaTemplate.send(topic, userResetPasswordDto);
    }

    public void sendOperatorRegisterMessage(String topic, OperatorRegisterDto operatorRegisterDto) {
        operatorRegisterDtoKafkaTemplate.send(topic, operatorRegisterDto);
    }
}
