package az.ingress.userms.service;
import az.ingress.userms.model.dto.ChangePasswordDto;
import az.ingress.userms.model.dto.ResetPasswordDto;
import jakarta.servlet.http.HttpServletResponse;

public interface PasswordService {

    void changePassword(ChangePasswordDto dto, String token, HttpServletResponse response);

    void forgetPassword(String email);

    void setNewPassword(String token, ResetPasswordDto passwordDto);
}
