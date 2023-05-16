package com.app.diamondhotelbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private int capacity;

    @JsonProperty("price_per_hotel_night")
    private BigDecimal pricePerHotelNight;

    private String image;

    @JsonProperty("equipment_list")
    @ElementCollection
    @CollectionTable(name = "room_equipment", joinColumns = @JoinColumn(name = "room_type_id"))
    @Column(name = "name")
    private List<String> equipmentList;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "roomType", cascade = CascadeType.ALL)
    private List<Room> roomList;
}
