package com.car.business.mappers;

import com.car.business.domain.BaseEntity;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class MapperUtils {

    private MapperUtils() {}

    public static <E extends BaseEntity<String>> List<String> toIds(Collection<E> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(BaseEntity::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
