package az.ingress.notificationms.service.listener.impl;
import az.ingress.common.kafka.OperatorRegisterDto;
import az.ingress.notificationms.model.entity.Notification;
import az.ingress.notificationms.model.enums.NotificationState;
import az.ingress.notificationms.repository.NotificationRepository;
import az.ingress.notificationms.service.MailService;
import az.ingress.notificationms.service.listener.OperatorRegisterListenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OperatorRegisterListenerServiceImpl implements OperatorRegisterListenerService {

    private final MailService mailService;
    private final NotificationRepository notificationRepository;

    private static final String SUBJECT = "User register for Operator";

    @Override
    public void sendNotificationEmail(OperatorRegisterDto operatorRegisterDto) {
        String link = "http://localhost:8083/api/v1/operators/admin/operator/approval";
        String message = "Dear Admin,\n\n" +
                "A new operator registration request has been made. Below are the details of the operator:\n\n" +
                "First Name: " + operatorRegisterDto.firstName() + "\n" +
                "Last Name: " + operatorRegisterDto.lastName() + "\n" +
                "Email: " + operatorRegisterDto.email() + "\n\n" +
                "To approve this request and assign the operator role, please click the link below:\n\n" +
                link + "\n\n" +
                "If you did not initiate this request, please disregard this email.\n\n" +
                "Best regards,\n" +
                "The Flight Application Team";
        operatorRegisterDto.adminEmails().forEach(adminEmail -> mailService.sendMail( adminEmail, SUBJECT, message));
    }

    @Override
    public void sendNotificationApp(OperatorRegisterDto operatorRegisterDto) {

        String notificationMessage = "A new operator registration request has been made.\n\n" +
                "Details of the operator:\n" +
                "First Name: " + operatorRegisterDto.firstName() + "\n" +
                "Last Name: " + operatorRegisterDto.lastName() + "\n" +
                "Email: " + operatorRegisterDto.email() + "\n\n" +
                "Please review and approve the request if necessary.";


        Notification notification = Notification.builder()
                .message(notificationMessage)
                .state(NotificationState.UNREAD)
                .createDate(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

}
