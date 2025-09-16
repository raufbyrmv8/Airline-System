package az.ingress.userms.config;
import az.ingress.common.config.JwtSessionData;
import az.ingress.common.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationService extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final JwtSessionData jwtSessionData;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")){
                String token = header.substring(7);
                Jws<Claims> claimsJws = jwtService.parseToken(token);
                Authentication authentication = getAuthentication(claimsJws.getPayload(), response);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }else{
                jwtSessionData.setUserId(0L);
                jwtSessionData.setUsername("anonymous");
                jwtSessionData.setRole("anonymous");
            }
            filterChain.doFilter(request, response);
        } catch (JwtException | BadCredentialsException ex){
            SecurityContextHolder.clearContext();
            throw new InsufficientAuthenticationException("Invalid JWT", ex);
        }
    }

    private Authentication getAuthentication(Claims claims, HttpServletResponse response){
        try {
            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);
            Long userId = claims.get("userId", Long.class);

            jwtSessionData.setUsername(username);
            jwtSessionData.setRole(role);
            jwtSessionData.setUserId(userId);

            var authorities = List.of(new SimpleGrantedAuthority(role));
            return new UsernamePasswordAuthenticationToken(username, null, authorities);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid token", e);
        }
    }
}
