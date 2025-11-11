package com.car.business.dto;

import com.car.business.dto.ContactApiDto;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class UsuarioApiDto {

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String documentType;
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
    private List<ContactApiDto> contacts;

}
