package com.fooddelivery.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * User entity - maps to the 'users' table in MySQL
 * Stores registered user information
 */
@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Email must be unique - used as login identifier
    @Column(nullable = false, unique = true)
    private String email;

    // Password stored as BCrypt hash (never plain text)
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    // Role: USER or ADMIN (for future extension)
    @Column(nullable = false)
    private String role = "USER";
}
