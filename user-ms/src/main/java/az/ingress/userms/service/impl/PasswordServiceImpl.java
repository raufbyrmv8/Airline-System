package az.ingress.userms.service.impl;
import az.ingress.common.model.exception.ApplicationException;
import az.ingress.common.service.JwtService;
import az.ingress.userms.model.dto.ChangePasswordDto;
import az.ingress.userms.model.dto.ResetPasswordDto;
import az.ingress.userms.model.entity.User;
import az.ingress.userms.model.entity.Verification;
import az.ingress.userms.model.enums.TokenType;
import az.ingress.userms.repository.UserRepository;
import az.ingress.userms.repository.VerificationRepository;
import az.ingress.userms.service.PasswordService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static az.ingress.userms.model.enums.Exceptions.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationRepository tokenRepository;
    private final JwtService jwtService;
    @Value("${application.token.confirmation.password-reset-expiration-time}")
    private Long PASSWORD_RESET_EXPIRATION_TIME;
//    @Value("${kafka.topic.reset-password}")
    private String RESET_PASSWORD_TOPIC;

    @Override
    public void changePassword(ChangePasswordDto dto, String token, HttpServletResponse response) {
        String tkn = token.substring(7);
        String email = jwtService.getUsername(tkn);
        userRepository.findByEmail(email).ifPresentOrElse(
                user -> {
                    if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
                        throw new ApplicationException(PASSWORD_IS_INCORRECT_EXCEPTION);
                    }
                    checkIfNewPasswordsMatch(dto.getNewPassword(), dto.getRepeatPassword());
                    user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
                    userRepository.save(user);
                },
                ()-> new ApplicationException(NOT_FOUND));
    }

    @Override
    public void forgetPassword(String email) {
        userRepository.findByEmail(email).ifPresent( user -> {
            String token = generateToken();
            createPasswordResetTokenForUser(user, token);
//            kafkaProducer.sendPasswordResetMessage(RESET_PASSWORD_TOPIC, new UserResetPasswordDto(user.getEmail(), user.getFirstName(), user.getLastName(), token));
        });
    }

    @Override
    public void setNewPassword(String token, ResetPasswordDto passwordDto) {
            Verification passToken =
                    tokenRepository.getValidToken(token, TokenType.PASSWORD_RESET.name()).orElseThrow(()-> new ApplicationException(TOKEN_NOT_FOUND_EXCEPTION));
            checkIfPasswordTokenExpired(passToken);

            User resetUser = userRepository.findById(passToken.getUser().getId()).orElseThrow(()-> new ApplicationException(NOT_FOUND));
            checkIfNewPasswordsMatch(passwordDto.getNewPassword(), passwordDto.getRepeatPassword());
            resetUser.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));

            passToken.setIsExpired(false);
            tokenRepository.save(passToken);
            userRepository.save(resetUser);
    }

    private void createPasswordResetTokenForUser(User user, String token) {
        Verification verificationToken = Verification.builder()
                .token(token)
                .user(user)
                .type(TokenType.PASSWORD_RESET)
                .isUsed(false)
                .isExpired(false)
                .expirationTime(LocalDateTime.now().plusSeconds(PASSWORD_RESET_EXPIRATION_TIME))
                .build();

        tokenRepository.save(verificationToken);
    }

    private void checkIfNewPasswordsMatch(String password, String repeatPassword) {
        if (!password.equals(repeatPassword)) {
            throw new ApplicationException(PASSWORD_MISMATCH_EXCEPTION);
        }
    }

    private void checkIfPasswordTokenExpired(Verification verificationToken) {
        if (verificationToken.getIsExpired()) {
            throw new ApplicationException(TOKEN_EXPIRED_EXCEPTION);
        }
    }

    private String generateToken(){
        return UUID.randomUUID().toString();
    }
}
