package com.fooddelivery.service;

import com.fooddelivery.dto.OrderRequest;
import com.fooddelivery.entity.CartItem;
import com.fooddelivery.entity.Order;
import com.fooddelivery.entity.User;
import com.fooddelivery.repository.CartItemRepository;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * OrderService - handles order placement and history
 *
 * Place Order Flow:
 * 1. Get user's cart items
 * 2. Calculate total amount
 * 3. Snapshot the order items as JSON string
 * 4. Save order to DB
 * 5. Clear the cart
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Place an order from the user's current cart
     */
    @Transactional
    public Order placeOrder(String email, OrderRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get all cart items
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty. Add items before placing order.");
        }

        // Calculate total: sum of (price × quantity) for each item
        double total = cartItems.stream()
                .mapToDouble(item ->
                        item.getMenuItem().getPrice() * item.getQuantity())
                .sum();

        // Create a snapshot of items (simple string representation)
        // In production, you'd have an OrderItem table
        String snapshot = cartItems.stream()
                .map(item -> String.format("%s x%d @ ₹%.2f",
                        item.getMenuItem().getName(),
                        item.getQuantity(),
                        item.getMenuItem().getPrice()))
                .collect(Collectors.joining(", "));

        // Create the order
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(total);
        order.setStatus("PENDING");
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setItemsSnapshot(snapshot);
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Clear the cart after successful order
        cartItemRepository.deleteByUserId(user.getId());

        return savedOrder;
    }

    /** Get order history for a user (newest first) */
    public List<Order> getOrderHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    /** Get a specific order by ID */
    public Order getOrderById(Long orderId, String email) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Security: only the order's owner can view it
        if (!order.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        return order;
    }
}
