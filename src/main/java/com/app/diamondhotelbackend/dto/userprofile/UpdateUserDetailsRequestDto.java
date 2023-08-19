package com.app.diamondhotelbackend.dto.userprofile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserDetailsRequestDto {

    private String firstname;

    private String lastname;

    private int age;

    private String country;

    @JsonProperty("passport_number")
    private String passportNumber;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String city;

    private String street;

    @JsonProperty("postal_code")
    private String postalCode;
}
