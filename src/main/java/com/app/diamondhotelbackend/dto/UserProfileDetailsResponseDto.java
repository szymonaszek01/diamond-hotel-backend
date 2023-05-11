package com.app.diamondhotelbackend.dto;

import com.app.diamondhotelbackend.entity.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDetailsResponseDto {

    String jwt;

    UserProfile userProfile;
}
