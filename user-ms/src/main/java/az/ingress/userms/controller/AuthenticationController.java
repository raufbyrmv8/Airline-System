package az.ingress.userms.controller;

import az.ingress.userms.model.dto.ForgetPasswordDto;
import az.ingress.userms.model.dto.request.LoginRequestDto;
import az.ingress.userms.model.dto.request.UserRequestDto;
import az.ingress.userms.model.dto.response.TokenResponseDto;
import az.ingress.userms.service.AuthenticationService;
import az.ingress.userms.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final PasswordService passwordService;
    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody UserRequestDto userRequestDto) {
        authenticationService.createUser(userRequestDto);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/verify")
    public ResponseEntity<Void> verifyUser(@RequestParam("token") String token) {
        authenticationService.verifyUser(token);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto dto){
        return ResponseEntity.ok(authenticationService.login(dto));
    }

    @GetMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@RequestParam String refreshToken){
        return ResponseEntity.ok(authenticationService.refresh(refreshToken));
    }

    @PostMapping("/forget-password")
    public ResponseEntity<Void> forgetPassword(@RequestBody ForgetPasswordDto forgetPasswordDto) {
        passwordService.forgetPassword(forgetPasswordDto.getEmail());
        return ResponseEntity.ok().build();
    }
}
