package com.car.business.mappers;

import com.car.business.domain.*;
import com.car.business.domain.enums.RolUsuario;
import com.car.business.domain.enums.TipoDocumento;
import com.car.business.domain.enums.TipoTelefono;
import com.car.business.domain.enums.TipoContacto;
import com.car.business.dto.ContactApiDto;
import com.car.business.dto.UsuarioApiDto;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mapper(componentModel = "spring",
        uses = {EntityReferenceResolver.class})
public abstract class UsuarioApiMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "nombreUsuario", source = "email")
    @Mapping(target = "clave", source = "password")
    @Mapping(target = "rolUsuario", expression = "java(com.car.business.domain.enums.RolUsuario.CLIENTE)")
    @Mapping(target = "persona", expression = "java(dto.getAccommodationAddress() != null && !dto.getAccommodationAddress().isEmpty() ? toCliente(dto) : toPersona(dto))")
    public abstract Usuario toUsuario(UsuarioApiDto dto);

    @Mapping(target = "id", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "nombre", source = "firstName")
    @Mapping(target = "apellido", source = "lastName")
    @Mapping(target = "fechaNacimiento", source = "dateOfBirth")
    @Mapping(target = "tipoDocumento", expression = "java(mapTipoDocumento(dto.getDocumentType()))")
    @Mapping(target = "numeroDocumento", source = "documentNumber")
    @Mapping(target = "contactos", expression = "java(mapContactos(dto.getContacts(), dto.getEmail()))")
    @Mapping(target = "direccion", expression = "java(toDireccion(dto))")
    @Mapping(target = "imagen", expression = "java(toImagen(dto))")
    public abstract Persona toPersona(UsuarioApiDto dto);

    @Mapping(target = "id", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "nombre", source = "firstName")
    @Mapping(target = "apellido", source = "lastName")
    @Mapping(target = "fechaNacimiento", source = "dateOfBirth")
    @Mapping(target = "tipoDocumento", expression = "java(mapTipoDocumento(dto.getDocumentType()))")
    @Mapping(target = "numeroDocumento", source = "documentNumber")
    @Mapping(target = "contactos", expression = "java(mapContactos(dto.getContacts(), dto.getEmail()))")
    @Mapping(target = "direccion", expression = "java(toDireccion(dto))")
    @Mapping(target = "imagen", expression = "java(toImagen(dto))")
    @Mapping(target = "direccionEstadia", source = "accommodationAddress")
    @Mapping(target = "nacionalidad", expression = "java(mapNacionalidad(dto.getCountry()))")
    public abstract Cliente toCliente(UsuarioApiDto dto);

    @AfterMapping
    protected void setPersonaOnContactos(@MappingTarget Persona persona) {
        if (persona.getContactos() != null) {
            persona.getContactos().forEach(contacto -> contacto.setPersona(persona));
        }
    }

    /**
     * Maps geographical information from DTO to Localidad entity.
     * IMPORTANT: This implementation creates new instances of Pais, Provincia, Departamento, and Localidad
     * every time. In a production environment, these entities should typically be looked up from
     * the database to ensure uniqueness and data integrity.
     */
    protected Localidad mapLocalidad(String localityName, String postalCode, String departmentName, String provinceName, String countryName) {
        if (localityName == null || localityName.isEmpty()) {
            return null;
        }

        Pais pais = mapPais(countryName);
        Provincia provincia = mapProvincia(provinceName, pais);
        Departamento departamento = mapDepartamento(departmentName, provincia);

        Localidad localidad = new Localidad();
        localidad.setId(UUID.randomUUID().toString());
        localidad.setNombre(localityName);
        localidad.setCodigoPostal(postalCode);
        localidad.setDepartamento(departamento);
        localidad.setEliminado(false);
        return localidad;
    }

    protected Departamento mapDepartamento(String departmentName, Provincia provincia) {
        if (departmentName == null || departmentName.isEmpty()) {
            return null;
        }
        Departamento departamento = new Departamento();
        departamento.setId(UUID.randomUUID().toString());
        departamento.setNombre(departmentName);
        departamento.setProvincia(provincia);
        departamento.setEliminado(false);
        return departamento;
    }

    protected Provincia mapProvincia(String provinceName, Pais pais) {
        if (provinceName == null || provinceName.isEmpty()) {
            return null;
        }
        Provincia provincia = new Provincia();
        provincia.setId(UUID.randomUUID().toString());
        provincia.setNombre(provinceName);
        provincia.setPais(pais);
        provincia.setEliminado(false);
        return provincia;
    }

    protected Pais mapPais(String countryName) {
        if (countryName == null || countryName.isEmpty()) {
            return null;
        }
        Pais pais = new Pais();
        pais.setId(UUID.randomUUID().toString());
        pais.setNombre(countryName);
        pais.setEliminado(false);
        return pais;
    }

    protected Nacionalidad mapNacionalidad(String countryName) {
        if (countryName == null || countryName.isEmpty()) {
            return null;
        }
        Nacionalidad nacionalidad = new Nacionalidad();
        nacionalidad.setId(UUID.randomUUID().toString());
        nacionalidad.setNombre(countryName); // Assuming nationality name is the same as country name
        nacionalidad.setEliminado(false);
        return nacionalidad;
    }

}