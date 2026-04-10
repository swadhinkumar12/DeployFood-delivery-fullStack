package com.fooddelivery.service;

import com.fooddelivery.dto.OrderRequest;
import com.fooddelivery.entity.CartItem;
import com.fooddelivery.entity.Order;
import com.fooddelivery.entity.OrderItem;
import com.fooddelivery.entity.Restaurant;
import com.fooddelivery.entity.User;
import com.fooddelivery.exception.ForbiddenOperationException;
import com.fooddelivery.repository.CartItemRepository;
import com.fooddelivery.repository.OrderRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;

/**
 * OrderService - handles order placement and history
 *
 * Place Order Flow:
 * 1. Get user's cart items
 * 2. Calculate total amount
 * 3. Save immutable order item rows
 * 4. Save order to DB
 * 5. Clear the cart
 */
@Service
public class OrderService {

        private static final Set<String> ALLOWED_STATUSES = Set.of(
                        "PENDING", "CONFIRMED", "PREPARING", "DELIVERED", "CANCELLED"
        );

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

                Restaurant restaurant = cartItems.get(0).getMenuItem().getRestaurant();
                boolean multipleRestaurants = cartItems.stream()
                                .map(item -> item.getMenuItem().getRestaurant().getId())
                                .distinct()
                                .count() > 1;
                if (multipleRestaurants) {
                        throw new RuntimeException("Order contains multiple restaurants. Please checkout one restaurant at a time.");
                }

        // Calculate total: sum of (price × quantity) for each item
        double total = cartItems.stream()
                .mapToDouble(item ->
                        item.getMenuItem().getPrice() * item.getQuantity())
                .sum();

        // Create the order
        Order order = new Order();
        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setTotalAmount(total);
        order.setStatus("PENDING");
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = cartItems.stream().map(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(item.getMenuItem());
            orderItem.setItemName(item.getMenuItem().getName());
                        orderItem.setItemType(item.getMenuItem().getItemType());
            orderItem.setUnitPrice(item.getMenuItem().getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setLineTotal(item.getMenuItem().getPrice() * item.getQuantity());
            return orderItem;
        }).toList();
        order.setItems(orderItems);

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
                        throw new ForbiddenOperationException("You cannot view another user's order");
        }

        return order;
    }

        public List<Order> getSellerOrders(String email) {
                User seller = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                return orderRepository.findByRestaurantSellerIdOrderByCreatedAtDesc(seller.getId());
        }

        @Transactional
        public Order updateSellerOrderStatus(Long orderId, String status, String email) {
                User seller = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new RuntimeException("Order not found"));

                if (!order.getRestaurant().getSeller().getId().equals(seller.getId())) {
                        throw new ForbiddenOperationException("You cannot update another seller's order");
                }

                String normalizedStatus = status == null ? "" : status.trim().toUpperCase();
                if (!ALLOWED_STATUSES.contains(normalizedStatus)) {
                        throw new RuntimeException("Invalid order status");
                }

                order.setStatus(normalizedStatus);
                return orderRepository.save(order);
        }
}
