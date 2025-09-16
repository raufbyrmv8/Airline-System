package az.ingress.userms.service;

import az.ingress.userms.model.dto.request.LoginRequestDto;
import az.ingress.userms.model.dto.request.UserRequestDto;
import az.ingress.userms.model.dto.response.TokenResponseDto;

public interface AuthenticationService {
    void createUser(UserRequestDto userRequestDto);

    void verifyUser(String token);

    TokenResponseDto login(LoginRequestDto dto);

    TokenResponseDto refresh(String refreshToken);
}
