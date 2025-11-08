package com.car.business.mappers;

import com.car.business.domain.BaseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class EntityReferenceResolver {

    @PersistenceContext
    private EntityManager entityManager;

    public <E extends BaseEntity<String>> E getReference(Class<E> clazz, String id) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(clazz, id);
    }

    public <E extends BaseEntity<String>> List<E> getReferences(Class<E> clazz, List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return ids.stream()
                .filter(Objects::nonNull)
                .map(id -> entityManager.getReference(clazz, id))
                .collect(Collectors.toList());
    }
}
