package com.car.clientead.client.dto.registro;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.car.clientead.client.dto.enums.TipoDocumento;

import lombok.Data;

@Data
public class UsuarioRegistroDto {

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private TipoDocumento documentType;
    private String documentNumber;

    private String photoBase64;
    private String photoContentType;

    private String street;
    private String number;
    private String locality;
    private String postalCode;
    private String neighborhood;
    private String block;
    private String floor;
    private String apartment;
    private String landmarks;
    private String department;
    private String province;
    private String country;
    private String accommodationAddress;

    private String email;
    private String password;
    private String confirmPassword;

    private List<ContactoRegistroDto> contacts = new ArrayList<>();
}
