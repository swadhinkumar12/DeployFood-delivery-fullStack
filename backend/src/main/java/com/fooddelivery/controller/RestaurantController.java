package com.fooddelivery.controller;

import com.fooddelivery.dto.ApiResponse;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * RestaurantController - CRUD for restaurants
 *
 * Endpoints:
 *   GET    /api/restaurants       - list all restaurants
 *   GET    /api/restaurants/{id}  - get one restaurant
 *   POST   /api/restaurants       - add restaurant (admin)
 *   PUT    /api/restaurants/{id}  - update restaurant
 *   DELETE /api/restaurants/{id}  - delete restaurant
 */
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    /** List all restaurants - used on Home page */
    @GetMapping
    public ResponseEntity<ApiResponse> getAllRestaurants() {
        return ResponseEntity.ok(
            ApiResponse.ok("Restaurants fetched", restaurantService.getAllRestaurants()));
    }

    /** Get single restaurant by ID */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getRestaurant(@PathVariable Long id) {
        return ResponseEntity.ok(
            ApiResponse.ok("Restaurant fetched", restaurantService.getRestaurantById(id)));
    }

    /** Add a new restaurant */
    @PostMapping
    public ResponseEntity<ApiResponse> addRestaurant(@RequestBody Restaurant restaurant) {
        return ResponseEntity.ok(
            ApiResponse.ok("Restaurant added", restaurantService.addRestaurant(restaurant)));
    }

    /** Update restaurant details */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateRestaurant(
            @PathVariable Long id,
            @RequestBody Restaurant restaurant) {
        return ResponseEntity.ok(
            ApiResponse.ok("Restaurant updated", restaurantService.updateRestaurant(id, restaurant)));
    }

    /** Delete a restaurant */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.ok(ApiResponse.ok("Restaurant deleted"));
    }
}
