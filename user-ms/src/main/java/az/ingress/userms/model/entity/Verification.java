package az.ingress.userms.model.entity;

import az.ingress.common.model.entity.AbstractEntity;
import az.ingress.userms.model.enums.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

@Entity(name = "verification")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Verification extends AbstractEntity {
    private String token;
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;
    @Enumerated(EnumType.STRING)
    private TokenType type;
    private Boolean isUsed;
    private Boolean isExpired;
    private LocalDateTime expirationTime;
}
