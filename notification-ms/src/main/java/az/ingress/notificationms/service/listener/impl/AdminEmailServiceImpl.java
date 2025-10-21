package az.ingress.notificationms.service.listener.impl;

import az.ingress.common.kafka.AdminNotificationDto;
import az.ingress.notificationms.service.MailService;
import az.ingress.notificationms.service.listener.AdminEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminEmailServiceImpl implements AdminEmailService {

    private final MailService mailService;

    @Override
    public void sendNotification(AdminNotificationDto adminNotificationDto) {
        String rejectLink = "http://localhost:8081/flight-ms/flights/reject/" + adminNotificationDto.flightId();
        String approveLink = "http://localhost:8081/flight-ms/flights/approve/" + adminNotificationDto.flightId();
        String checkLink = "http://localhost:8081/flight-ms/flights/" + adminNotificationDto.flightId();

        String message = String.format(
                """
                        Dear Admin Adminov,\s

                        New flight(%s) created by operator(%s).

                        You can approve or reject the flight. If you want to check the flight status, enter the link below:
                        %s

                        To approve, click here: %s

                        To reject, click here: %s

                        Best regards,
                        The Flight Application Team""",
                adminNotificationDto.flightId(),
                adminNotificationDto.operatorId(),
                checkLink,
                approveLink,
                rejectLink
        );
        mailService.sendMail("2004osm94@gmail.com", adminNotificationDto.subject(), message); //admin
    }
}
