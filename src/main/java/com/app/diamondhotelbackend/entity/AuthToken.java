package com.app.diamondhotelbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne()
    private UserProfile userProfile;

    @Column(unique = true)
    private String accessValue;

    @Column(unique = true)
    private String refreshValue;
}
