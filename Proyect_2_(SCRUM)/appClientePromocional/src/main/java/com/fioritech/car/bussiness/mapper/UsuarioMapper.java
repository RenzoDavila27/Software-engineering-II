package com.fioritech.car.bussiness.mapper;

import com.fioritech.car.bussiness.dto.RegistrationForm;
import com.fioritech.car.bussiness.dto.UsuarioApiDto;
import com.fioritech.car.bussiness.dto.UsuarioDto;
import com.fioritech.car.bussiness.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(source = "photo", target = "photoBase64", qualifiedByName = "fileToBase64")
    @Mapping(source = "photo", target = "photoContentType", qualifiedByName = "fileToContentType")
    UsuarioApiDto registrationFormToApiDto(RegistrationForm form);


    @Named("fileToBase64")
    default String fileToBase64(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        // Convierte el archivo a Base64
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

    @Named("fileToContentType")
    default String fileToContentType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        return file.getContentType();
    }
}
