package az.ingress.userms.controller;

import az.ingress.userms.model.dto.ChangePasswordDto;
import az.ingress.userms.model.dto.ResetPasswordDto;
import az.ingress.userms.service.PasswordService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/password")
public class PasswordController {
    private final PasswordService passwordService;

    @PutMapping("/change")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordDto dto,
                                               @RequestHeader(value = "Authorization") String token,
                                               HttpServletResponse response) {
        passwordService.changePassword(dto, token, response);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reset")
    public ResponseEntity<Void> setNewPassword(@RequestParam String token, @RequestBody @Valid ResetPasswordDto passwordDto) {
        passwordService.setNewPassword(token, passwordDto);
        return ResponseEntity.ok().build();
    }
}
