package com.fooddelivery.service;

import com.fooddelivery.entity.MenuItem;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.repository.MenuItemRepository;
import com.fooddelivery.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MenuService - handles food item CRUD for restaurants
 */
@Service
public class MenuService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    /** Get all menu items for a restaurant */
    public List<MenuItem> getMenuByRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurantIdAndAvailableTrue(restaurantId);
    }

    /** Add a new food item to a restaurant */
    public MenuItem addMenuItem(Long restaurantId, MenuItem item) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found: " + restaurantId));
        item.setRestaurant(restaurant);
        return menuItemRepository.save(item);
    }

    /** Get a single menu item by ID */
    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found: " + id));
    }

    /** Update a menu item */
    public MenuItem updateMenuItem(Long id, MenuItem updated) {
        MenuItem existing = getMenuItemById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setCategory(updated.getCategory());
        existing.setAvailable(updated.getAvailable());
        return menuItemRepository.save(existing);
    }

    /** Delete a menu item */
    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }
}
