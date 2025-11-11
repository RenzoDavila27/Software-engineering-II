package com.fioritech.car.bussiness.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class RegistrationForm {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String documentType;
    private String documentNumber;
    private MultipartFile photo;
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
    private List<ContactDto> contacts = new ArrayList<>();
}
