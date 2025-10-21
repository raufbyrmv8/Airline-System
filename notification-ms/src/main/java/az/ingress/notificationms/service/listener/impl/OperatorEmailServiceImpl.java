package az.ingress.notificationms.service.listener.impl;

import az.ingress.common.kafka.OperatorNotificationDto;
import az.ingress.notificationms.service.MailService;
import az.ingress.notificationms.service.listener.OperatorEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperatorEmailServiceImpl implements OperatorEmailService {

    private final MailService mailService;


    @Override
    public void sendNotification(OperatorNotificationDto operatorNotificationDto) {
        String checkLink = "http://localhost:8081/flight-ms/flights/" + operatorNotificationDto.flightId();
        String message = String.format("""
                        Dear operator %s %s,\s

                        %s

                        Feedback: %s
                        
                        If you want to check the flight status, enter the link below: %s
                        
                        Best regards,
                        The Flight Application Team
                        """,
                operatorNotificationDto.operatorName(),
                operatorNotificationDto.operatorSurname(),
                operatorNotificationDto.subject(),
                operatorNotificationDto.feedbackMessage(),
                checkLink
        );
        mailService.sendMail(operatorNotificationDto.operatorEmail(), operatorNotificationDto.subject(), message);

    }
}
