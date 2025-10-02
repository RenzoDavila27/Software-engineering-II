package com.fioritech.demo.bussines.logic.service.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MigracionResultado {

    private int registrosProcesados;
    private int registrosCreados;
    private int registrosOmitidos;
    private final List<String> errores = new ArrayList<>();
    private final List<String> advertencias = new ArrayList<>();

    public void incrementarProcesados() {
        registrosProcesados++;
    }

    public void incrementarCreados() {
        registrosCreados++;
    }

    public void incrementarOmitidos() {
        registrosOmitidos++;
    }

    public void agregarError(String error) {
        errores.add(error);
    }

    public void agregarAdvertencia(String advertencia) {
        advertencias.add(advertencia);
    }

    public int getRegistrosProcesados() {
        return registrosProcesados;
    }

    public int getRegistrosCreados() {
        return registrosCreados;
    }

    public int getRegistrosOmitidos() {
        return registrosOmitidos;
    }

    public List<String> getErrores() {
        return Collections.unmodifiableList(errores);
    }

    public List<String> getAdvertencias() {
        return Collections.unmodifiableList(advertencias);
    }

    public boolean tieneErrores() {
        return !errores.isEmpty();
    }

    public boolean tieneAdvertencias() {
        return !advertencias.isEmpty();
    }
}
