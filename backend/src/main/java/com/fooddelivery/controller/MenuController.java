package com.fooddelivery.controller;

import com.fooddelivery.dto.ApiResponse;
import com.fooddelivery.entity.MenuItem;
import com.fooddelivery.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * MenuController - manage food items per restaurant
 *
 * Endpoints:
 *   GET    /api/menu/restaurant/{restaurantId}  - get menu for a restaurant
 *   POST   /api/menu/restaurant/{restaurantId}  - add item to restaurant menu
 *   PUT    /api/menu/{id}                       - update a menu item
 *   DELETE /api/menu/{id}                       - remove a menu item
 */
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /** Get all available menu items for a restaurant */
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse> getMenu(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(
            ApiResponse.ok("Menu fetched", menuService.getMenuByRestaurant(restaurantId)));
    }

    /** Add a new food item to a restaurant */
    @PostMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse> addMenuItem(
            @PathVariable Long restaurantId,
            @RequestBody MenuItem menuItem) {
        return ResponseEntity.ok(
            ApiResponse.ok("Menu item added", menuService.addMenuItem(restaurantId, menuItem)));
    }

    /** Update a food item's details */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateMenuItem(
            @PathVariable Long id,
            @RequestBody MenuItem menuItem) {
        return ResponseEntity.ok(
            ApiResponse.ok("Menu item updated", menuService.updateMenuItem(id, menuItem)));
    }

    /** Delete a food item from menu */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteMenuItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.ok(ApiResponse.ok("Menu item deleted"));
    }
}
