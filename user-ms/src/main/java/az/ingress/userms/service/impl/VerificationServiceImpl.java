package az.ingress.userms.service.impl;
import az.ingress.common.model.exception.ApplicationException;
import az.ingress.userms.model.entity.User;
import az.ingress.userms.model.entity.Verification;
import az.ingress.userms.model.enums.Exceptions;
import az.ingress.userms.model.enums.TokenType;
import az.ingress.userms.repository.VerificationRepository;
import az.ingress.userms.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final VerificationRepository verificationRepository;
    @Value("${application.token.confirmation.expiration-time}")
    private Long EXPIRATION_TIME;

    @Override
    public String sendVerificationEmail(User user) {
        Verification verification = new Verification();
        verification.setUser(user);
        verification.setExpirationTime(LocalDateTime.now().plusSeconds(EXPIRATION_TIME));
        verification.setIsExpired(false);
        verification.setIsUsed(false);
        String token = generateToken();
        verification.setToken(token);
        verification.setType(TokenType.REGISTRATION);
        verificationRepository.save(verification);
        return token;
    }

    @Override
    public User getUserByValidToken(String token, TokenType type) {
        Verification verification = verificationRepository.getValidToken(token, type.name()).orElseThrow(() -> new ApplicationException(Exceptions.NOT_FOUND, token));
        verification.setIsUsed(true);
        verificationRepository.save(verification);
        return verification.getUser();
    }


    public String generateToken() {
        return UUID.randomUUID().toString();
    }
}
