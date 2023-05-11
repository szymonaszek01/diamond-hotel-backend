package com.app.diamondhotelbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {

    private String email;

    private String password;

    private String repeatedPassword;

    private String firstname;

    private String lastname;

    private int age;

    private String country;

    private String passportNumber;

    private String phoneNumber;

    private String city;

    private String street;

    private String postalCode;
}
