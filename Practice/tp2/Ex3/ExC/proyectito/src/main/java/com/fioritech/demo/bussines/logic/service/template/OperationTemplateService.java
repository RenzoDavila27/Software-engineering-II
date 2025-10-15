package com.fioritech.demo.bussines.logic.service.template;

/**
 * Template Method genérico para orquestar operaciones que reciben
 * un parámetro de entrada y producen un resultado.
 *
 * @param <I> Tipo de entrada requerido por la operación.
 * @param <O> Tipo de resultado producido por la operación.
 */
public abstract class OperationTemplateService<I, O> {

    /**
     * Template Method que define los pasos del procesamiento.
     */
    protected final O ejecutar(I entrada) {
        validarEntrada(entrada);
        preProcesar(entrada);
        O resultado = ejecutarOperacion(entrada);
        postProcesar(entrada, resultado);
        return resultado;
    }

    protected void validarEntrada(I entrada) {
        // Hook opcional
    }

    protected void preProcesar(I entrada) {
        // Hook opcional
    }

    protected abstract O ejecutarOperacion(I entrada);

    protected void postProcesar(I entrada, O resultado) {
        // Hook opcional
    }
}

