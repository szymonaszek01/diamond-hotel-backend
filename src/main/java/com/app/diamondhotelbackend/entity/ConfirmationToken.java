package com.app.diamondhotelbackend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty("user_profile")
    @ManyToOne()
    private UserProfile userProfile;

    @JsonProperty("access_value")
    @Column(unique = true)
    private String accessValue;

    @JsonProperty("created_at")
    @Column(nullable = false)
    private Date createdAt;

    @JsonProperty("expires_at")
    @Column(nullable = false)
    private Date expiresAt;

    @JsonProperty("confirmed_at")
    private Date confirmedAt;
}
