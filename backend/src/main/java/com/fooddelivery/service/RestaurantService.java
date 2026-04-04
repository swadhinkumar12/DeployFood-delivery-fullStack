package com.fooddelivery.service;

import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RestaurantService - business logic for restaurant management
 */
@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    /** Return all restaurants in the system */
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    /** Get a single restaurant by ID */
    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found: " + id));
    }

    /** Add a new restaurant */
    public Restaurant addRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    /** Update restaurant details */
    public Restaurant updateRestaurant(Long id, Restaurant updated) {
        Restaurant existing = getRestaurantById(id);
        existing.setName(updated.getName());
        existing.setLocation(updated.getLocation());
        existing.setCuisineType(updated.getCuisineType());
        existing.setRating(updated.getRating());
        existing.setDeliveryTime(updated.getDeliveryTime());
        return restaurantRepository.save(existing);
    }

    /** Delete a restaurant */
    public void deleteRestaurant(Long id) {
        restaurantRepository.deleteById(id);
    }
}
