package az.ingress.userms.model.entity;
import az.ingress.common.model.constant.Roles;
import jakarta.persistence.Entity;
import az.ingress.common.model.entity.AbstractEntity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity(name = "_user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class User extends AbstractEntity {
    private String email;
    private String password;
    private Boolean isActive;
    private Boolean isEnabled;
    @Enumerated(EnumType.STRING)
    private Roles role;
    private String firstName;
    private String lastName;
}
