package com.app.diamondhotelbackend.dto.userprofile.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfilePictureDetailsResponseDto {

    private String email;

    private byte[] image;
}
