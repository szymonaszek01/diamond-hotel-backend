package com.app.diamondhotelbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDto {

    @JsonProperty("token")
    private String token;

    @JsonProperty("new_password")
    private String newPassword;
}
