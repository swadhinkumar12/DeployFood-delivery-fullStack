package com.fooddelivery.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * Order entity - maps to 'orders' table
 * Represents a placed order by a user
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which user placed this order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Total price of the order
    @Column(nullable = false)
    private Double totalAmount;

    // Order status: PENDING, CONFIRMED, PREPARING, DELIVERED, CANCELLED
    @Column(nullable = false)
    private String status = "PENDING";

    // When was the order placed (auto-set)
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Delivery address for this order
    private String deliveryAddress;

    // Store order items as JSON string (simple approach)
    @Column(columnDefinition = "TEXT")
    private String itemsSnapshot;
}
