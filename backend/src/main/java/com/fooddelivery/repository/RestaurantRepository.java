package com.fooddelivery.repository;

import com.fooddelivery.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** RestaurantRepository - CRUD for restaurants table */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    // All standard CRUD methods come from JpaRepository
}
