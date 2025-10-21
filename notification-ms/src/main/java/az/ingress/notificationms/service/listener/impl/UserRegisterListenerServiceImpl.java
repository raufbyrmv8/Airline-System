package az.ingress.notificationms.service.listener.impl;
import az.ingress.common.kafka.UserRegisterDto;
import az.ingress.notificationms.service.MailService;
import az.ingress.notificationms.service.listener.UserRegisterListenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegisterListenerServiceImpl implements UserRegisterListenerService {
    private final MailService mailService;

    @Override
    public void sendNotification(UserRegisterDto dto) {
        String link = "http://localhost:8083/api/v1/auth/verify?token=" + dto.token();
        String message = "Welcome to our platform. Thank you for registering " + dto.firstName() + " " + dto.lastName() + ". Please verify your email. Please click the link below\n " + link;
        mailService.sendMail(dto.email(), "flight application register service", message);
    }
}
