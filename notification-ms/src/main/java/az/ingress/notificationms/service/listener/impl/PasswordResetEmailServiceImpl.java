package az.ingress.notificationms.service.listener.impl;

import az.ingress.common.kafka.UserResetPasswordDto;
import az.ingress.notificationms.service.MailService;
import az.ingress.notificationms.service.listener.PasswordResetEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetEmailServiceImpl implements PasswordResetEmailService {

    private final MailService mailService;

    private static final String SUBJECT = "Verification for password reset";

    @Override
    public void sendEmail(UserResetPasswordDto dto) {
        String link = "http://localhost:8083/api/v1/auth/password/reset?token=" + dto.token();
        String message = "Dear " + dto.firstName() + " " + dto.lastName() + ",\n\n" +
                "If you did not request a password reset, please disregard this email.\n\n" +
                "We received a request to reset your password. To reset your password, please click the link below:\n\n" +
                link + "\n\n" +
                "Best regards,\n" +
                "The Flight Application Team";
        mailService.sendMail(dto.email(), SUBJECT, message);
    }
}
