package az.ingress.notificationms.service.listener;


import az.ingress.common.kafka.UserResetPasswordDto;

public interface PasswordResetEmailService {
    void sendEmail(UserResetPasswordDto dto);
}
