package com.app.diamondhotelbackend.dto.userprofile.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileDetailsUpdateRequestDto {

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
