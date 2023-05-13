package com.app.diamondhotelbackend.dto;

import com.app.diamondhotelbackend.entity.UserProfile;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDetailsResponseDto {

    String jwt;

    @JsonProperty("user_profile")
    UserProfile userProfile;
}
