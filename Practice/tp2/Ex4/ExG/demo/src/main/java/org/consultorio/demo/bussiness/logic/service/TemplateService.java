package org.consultorio.demo.bussiness.logic.service;

import org.consultorio.demo.bussiness.domain.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public abstract class TemplateService<T extends TemplateEntity> {

    protected abstract JpaRepository<T, String> getRepository();

    @Transactional
    public T crear(T entity) {
        return getRepository().save(entity);
    }

    @Transactional
    public T modificar(T entity) {
        return getRepository().save(entity);
    }

    @Transactional
    public void eliminar(String id) {
        Optional<T> entityOpt = getRepository().findById(id);
        if (entityOpt.isPresent()) {
            T entity = entityOpt.get();
            entity.setEliminado(true);
            getRepository().save(entity);
        }
    }

    @Transactional(readOnly = true)
    public Optional<T> buscarPorId(String id) {
        return getRepository().findById(id);
    }

    @Transactional(readOnly = true)
    public List<T> listarTodos() {
        return getRepository().findAll();
    }

    @Transactional(readOnly = true)
    public List<T> listarActivos() {
        return getRepository().findAll().stream()
                .filter(entity -> !entity.isEliminado())
                .toList();
    }
}
