package az.ingress.userms.service;

import az.ingress.userms.model.entity.User;
import az.ingress.userms.model.enums.TokenType;

public interface VerificationService {
    String sendVerificationEmail(User user);


    User getUserByValidToken(String token, TokenType type);
}
