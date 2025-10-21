package az.ingress.notificationms.service;

public interface MailService {
    void sendMail(String to, String subject, String text);
}
