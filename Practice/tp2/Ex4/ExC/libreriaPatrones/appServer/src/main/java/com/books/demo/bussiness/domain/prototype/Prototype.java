package com.books.demo.bussiness.domain.prototype;

/**
 * Contrato genérico para objetos que admiten clonación basada en el patrón Prototype.
 *
 * @param <T> tipo concreto que implementa el prototipo
 */
public interface Prototype<T> {

    /**
     * Realiza una copia del objeto actual.
     *
     * @return nueva instancia clonada
     */
    T clonar();
}
