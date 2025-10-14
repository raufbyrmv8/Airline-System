package az.ingress.flightms.model.entity;


import az.ingress.common.model.entity.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Airline extends AbstractEntity {
    @Column(unique = true)
    String name;
}
