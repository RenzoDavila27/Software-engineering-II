package com.car.business.mappers;

import com.car.business.domain.BaseEntity;
import com.car.business.dto.BaseDto;

public interface BaseMapper<E extends BaseEntity<ID>, D extends BaseDto<ID>, ID> {

    D toDto(E entity);

    E toEntity(D dto);

    void updateEntity(D dto, E entity);
}
