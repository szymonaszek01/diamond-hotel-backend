package com.app.diamondhotelbackend.dto.userprofile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserImageResponseDto {

    private String email;

    private byte[] image;
}
