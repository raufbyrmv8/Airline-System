package az.ingress.userms.config;
import az.ingress.common.config.JwtSessionData;
import az.ingress.common.model.exception.Handler;
import az.ingress.common.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
@Import({Handler.class, JwtService.class})
public class BeanConfig {
    @Bean
    @RequestScope
    public JwtSessionData jwtSessionData() {
        return new JwtSessionData();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
