package com.app.diamondhotelbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class RoomTypeOpinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int rate;

    @ManyToOne
    private RoomType roomType;

    @ManyToOne
    private UserProfile userProfile;
}
