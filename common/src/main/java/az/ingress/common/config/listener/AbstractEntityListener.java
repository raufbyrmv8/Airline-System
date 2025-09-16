package az.ingress.common.config.listener;
import az.ingress.common.config.JwtSessionData;
import az.ingress.common.model.entity.AbstractEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AbstractEntityListener {

    @Autowired
    private JwtSessionData jwtSessionData;

    @PrePersist
    public void prePersist(AbstractEntity entity) {
        entity.setCreatedDate(LocalDateTime.now());
        entity.setCreatedBy(jwtSessionData.getUserId());
        if (entity.getStatus() == null) entity.setStatus(true);
    }

    @PreUpdate
    public void preUpdate(AbstractEntity entity) {
        entity.setUpdatedDate(LocalDateTime.now());
        entity.setUpdatedBy(jwtSessionData.getUserId());
    }
}
