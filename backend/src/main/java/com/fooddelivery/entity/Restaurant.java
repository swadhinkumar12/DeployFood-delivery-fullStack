package com.fooddelivery.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Restaurant entity - maps to the 'restaurants' table
 * Represents a restaurant in the system
 */
@Entity
@Table(name = "restaurants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    // Optional: cuisine type for display
    private String cuisineType;

    // Rating out of 5
    private Double rating = 4.0;

    // Delivery time in minutes
    private Integer deliveryTime = 30;
}
