package az.ingress.flightms.model.entity;
import az.ingress.common.model.entity.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Plane extends AbstractEntity {
    private String name;
    private Integer capacity;
    @ManyToOne
    private Airline airline;
    @ManyToMany
    private Set<PlanePlace> planePlaces;

}
