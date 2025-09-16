package az.ingress.common.model.entity;

import az.ingress.common.config.listener.AbstractEntityListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AbstractEntityListener.class)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @Column(updatable = false, nullable = false)
    protected LocalDateTime createdDate;
    @Column(insertable = false)
    protected LocalDateTime updatedDate;
    @Column(updatable = false, nullable = false)
    protected Long createdBy;
    @Column(insertable = false)
    protected Long updatedBy;
    protected Boolean status;
}
