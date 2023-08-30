package com.app.diamondhotelbackend.dto.auth.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountLoginRequestDto {

    private String email;

    private String password;
}
