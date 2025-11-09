package com.fioritech.car.mapper;

import com.fioritech.car.dto.UsuarioDto;
import com.fioritech.car.model.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioDto toDto(Usuario usuario);
    Usuario toEntity(UsuarioDto usuarioDto);
}
