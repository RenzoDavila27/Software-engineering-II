package com.books.demo.bussiness.logic.adapter;

import com.books.demo.bussiness.domain.Autor;
import com.books.demo.controller.rest.dto.AutorDto;
import org.springframework.stereotype.Component;

@Component
public class AutorAdapter implements DtoAdapter<AutorDto, Autor> {

    @Override
    public AutorDto toDto(Autor entity) {
        if (entity == null) {
            return null;
        }
        return new AutorDto(
                entity.getId(),
                entity.getNombre(),
                entity.getApellido(),
                entity.getBiografia(),
                entity.isEliminado()
        );
    }

    @Override
    public Autor toEntity(AutorDto dto) {
        if (dto == null) {
            return null;
        }
        Autor autor = new Autor();
        autor.setId(dto.getId());
        autor.setNombre(dto.getNombre());
        autor.setApellido(dto.getApellido());
        autor.setBiografia(dto.getBiografia());
        autor.setEliminado(dto.isEliminado());
        return autor;
    }
}
