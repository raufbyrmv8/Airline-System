package az.ingress.notificationms.service.listener.impl;
import az.ingress.common.model.dto.TicketMailDto;
import az.ingress.notificationms.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketCreateService {
    private final MailService mailService;
    public void sendNotification(TicketMailDto dto) {
        mailService.sendMail(dto.getEmail(), dto.getSubject(), dto.getTicketContent());
    }
}
