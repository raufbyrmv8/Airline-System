package az.ingress.flightms.config;
import az.ingress.common.model.constant.EndPoints;
import az.ingress.common.model.constant.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationService jwtAuthenticationService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        security
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                EndPoints.SWAGGER_V2,
                                EndPoints.SWAGGER_V3,
                                EndPoints.SWAGGER_V3_ALL,
                                EndPoints.SWAGGER_RESOURCE,
                                EndPoints.SWAGGER_RESOURCE_ALL,
                                EndPoints.SWAGGER_CONFIGURATION_UI,
                                EndPoints.SWAGGER_CONFIGURATION_SECURITY,
                                EndPoints.SWAGGER_UI,
                                EndPoints.SWAGGER_WEBJARS,
                                EndPoints.SWAGGER_UI_HTML).permitAll()
                        .requestMatchers("/api/v1/airline/**").hasAnyAuthority(Roles.ADMIN.name(), Roles.OPERATOR.name())
                        .requestMatchers("/api/v1/tickets/**").authenticated()
                        .requestMatchers("/api/v1/booking/**").authenticated()
                        .requestMatchers("/api/v1/files/**").authenticated()
                        .requestMatchers("/api/v1/flights/**").hasAnyAuthority(Roles.ADMIN.name(), Roles.OPERATOR.name())
                        .requestMatchers("/api/v1/planes/**").hasAnyAuthority(Roles.ADMIN.name(), Roles.OPERATOR.name())
                        .requestMatchers("api/v1/plane-place/**").hasAnyAuthority(Roles.ADMIN.name(), Roles.OPERATOR.name())
                        .anyRequest().denyAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationService, UsernamePasswordAuthenticationFilter.class);
        return security.build();
    }
}