package com.app.diamondhotelbackend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String name;

    private int adults;

    private int children;

    @JsonProperty("price_per_hotel_night")
    private BigDecimal pricePerHotelNight;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private byte[] image;

    @ElementCollection
    @CollectionTable(name = "room_equipment", joinColumns = @JoinColumn(name = "room_type_id"))
    @Column(name = "name")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<String> equipment;
}
