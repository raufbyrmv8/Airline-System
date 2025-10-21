package az.ingress.notificationms.service.impl;

import az.ingress.common.model.exception.ApplicationException;
import az.ingress.notificationms.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static az.ingress.notificationms.model.enums.Exceptions.APPLICATION_MAIL_MESSAGE_FAILURE;


@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;

    @Override
    public void sendMail(String to, String subject, String text) {
        try {
            log.info("Sending mail to: {} subject {}, text {}", to, subject, text);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException(APPLICATION_MAIL_MESSAGE_FAILURE, text);
        }
    }
}
