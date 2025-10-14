package az.ingress.flightms.model.entity;

import az.ingress.common.model.entity.AbstractEntity;
import az.ingress.flightms.model.enums.PlaceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanePlace extends AbstractEntity {
    private Integer place; // optional when creating a new plane place
    private Integer row; // optional when creating a new plane place
    @Column(nullable = false)
    private Integer placeNumber;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlaceType placeType;
}
