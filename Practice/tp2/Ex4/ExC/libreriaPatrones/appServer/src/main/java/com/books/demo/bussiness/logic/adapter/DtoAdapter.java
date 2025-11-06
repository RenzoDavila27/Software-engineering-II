package com.books.demo.bussiness.logic.adapter;

/**
 * Contrato base para adaptar entidades de dominio hacia DTOs y viceversa.
 *
 * @param <D> tipo de DTO
 * @param <E> tipo de entidad
 */
public interface DtoAdapter<D, E> {

    /**
     * Transforma una entidad de dominio a su representación DTO.
     *
     * @param entity entidad de dominio
     * @return DTO resultante
     */
    D toDto(E entity);

    /**
     * Transforma un DTO a su entidad de dominio equivalente.
     *
     * @param dto DTO fuente
     * @return entidad resultante
     */
    E toEntity(D dto);
}
