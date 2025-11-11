package com.fioritech.car.bussiness.service;

import com.fioritech.car.bussiness.dto.RegistrationForm;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegistrationService {

    public List<String> validate(RegistrationForm form) {
        List<String> errors = new ArrayList<>();

        if (!StringUtils.hasText(form.getFirstName())) {
            errors.add("Debe incluir un nombre");
        }
        if (!StringUtils.hasText(form.getLastName())) {
            errors.add("Debe incluir un apellido");
        }
        if (form.getDateOfBirth() == null) {
            errors.add("Debe incluir una fecha de nacimiento");
        }
        if (form.getDateOfBirth() != null && form.getDateOfBirth().isAfter(LocalDate.now())) {
            errors.add("La fecha de nacimiento debe ser del pasado");
        }
        if (!StringUtils.hasText(form.getDocumentType())) {
            errors.add("Debe seleccionar un tipo de documento");
        }
        if (!StringUtils.hasText(form.getDocumentNumber())) {
            errors.add("Debe incluir un numero de documento");
        }
        if (form.getPhoto() == null || form.getPhoto().isEmpty()) {
            errors.add("Debe incluir una foto");
        }
        if (form.getContacts() == null || form.getContacts().isEmpty()) {
            errors.add("Debe agregar al menos un contacto");
        }
        if (!StringUtils.hasText(form.getStreet())) {
            errors.add("Debe incluir una calle");
        }
        if (!StringUtils.hasText(form.getNumber())) {
            errors.add("Debe incluir un numero");
        }
        if (!StringUtils.hasText(form.getLocality())) {
            errors.add("Debe incluir una localidad");
        }
        if (!StringUtils.hasText(form.getPostalCode())) {
            errors.add("Debe incluir un codigo postal");
        }
        if (!StringUtils.hasText(form.getDepartment())) {
            errors.add("Debe incluir un departamento");
        }
        if (!StringUtils.hasText(form.getProvince())) {
            errors.add("Debe incluir una provincia");
        }
        if (!StringUtils.hasText(form.getCountry())) {
            errors.add("Debe incluir un pais");
        }
        if (!StringUtils.hasText(form.getAccommodationAddress())) {
            errors.add("Debe incluir una direccion de alojamiento");
        }
        if (!StringUtils.hasText(form.getEmail())) {
            errors.add("Debe incluir un email");
        }
        if (!StringUtils.hasText(form.getPassword())) {
            errors.add("Debe incluir una contraseña");
        }
        if (!StringUtils.hasText(form.getConfirmPassword())) {
            errors.add("Debe confirmar su contraseña");
        }
        if (StringUtils.hasText(form.getPassword()) && StringUtils.hasText(form.getConfirmPassword()) && !form.getPassword().equals(form.getConfirmPassword())) {
            errors.add("Las contraseñas tienen que coincidir");
        }

        return errors;
    }
}
