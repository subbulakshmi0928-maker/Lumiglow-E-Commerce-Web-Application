package com.cosmetics.app.controller;

import com.cosmetics.app.model.Order;
import com.cosmetics.app.model.OrderItem;
import com.cosmetics.app.model.Product;
import com.cosmetics.app.repository.OrderRepository;
import com.cosmetics.app.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private ProductRepository productRepo;

    // ===============================
    // CREATE ORDER
    // ===============================
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> body) {
        try {
            String email = body.get("email").toString();

            List<Map<String, Object>> items =
                    (List<Map<String, Object>>) body.get("items");

            if (items == null || items.isEmpty()) {
                return ResponseEntity.badRequest().body("Cart is empty");
            }

            double total = 0;
            List<OrderItem> orderItems = new ArrayList<>();

            for (Map<String, Object> item : items) {
                Long productId = Long.parseLong(item.get("id").toString());
                int qty = Integer.parseInt(item.get("qty").toString());
                Product product = productRepo.findById(productId).orElse(null);

                if (product != null) {
                    double price = product.getPrice();
                    total += price * qty;

                    OrderItem oi = new OrderItem();
                    oi.setProductName(product.getName());
                    oi.setPrice(price);
                    oi.setQuantity(qty);

                    // ✅ Set product image if your Product model has getImage()
                    // oi.setProductImage(product.getImage());

                    orderItems.add(oi);
                }
            }

            Order order = new Order();
            order.setUserEmail(email);
            order.setTotalPrice(total);
            order.setStatus("Pending");
            order.setOrderDate(LocalDateTime.now());
            order.setPaymentMethod(
                body.containsKey("paymentMethod")
                    ? body.get("paymentMethod").toString()
                    : "Online"
            );

            for (OrderItem oi : orderItems) {
                oi.setOrder(order);
            }
            order.setItems(orderItems);
            orderRepo.save(order);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Order placed successfully"
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Order failed: " + e.getMessage()
            ));
        }
    }

    // ===============================
    // GET ALL ORDERS
    // ===============================
    @Transactional
    @GetMapping("/orders")
    public ResponseEntity<?> getOrders() {
        try {
            List<Order> orders = orderRepo.findAll();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                Map.of("error", "Failed to fetch orders: " + e.getMessage())
            );
        }
    }

    // ===============================
    // GET ORDER DETAILS
    // ===============================
    @Transactional
    @GetMapping("/order-details/{id}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long id) {
        try {
            Optional<Order> order = orderRepo.findById(id);
            if (order.isPresent()) {
                return ResponseEntity.ok(order.get().getItems());
            } else {
                return ResponseEntity.status(404).body("Order not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                Map.of("error", "Failed to fetch order details: " + e.getMessage())
            );
        }
    }

    // ===============================
    // UPDATE ORDER STATUS
    // ===============================
    @Transactional
    @PutMapping("/update-order/{id}")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            Optional<Order> orderOpt = orderRepo.findById(id);

            if (orderOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Order not found");
            }

            if (body.get("status") == null || body.get("status").isBlank()) {
                return ResponseEntity.badRequest().body("Status is required");
            }

            Order order = orderOpt.get();
            order.setStatus(body.get("status"));
            orderRepo.save(order);

            // ✅ FIXED: return the updated Order object
            // so frontend can read response.status
            return ResponseEntity.ok(order);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(
                Map.of("error", "Failed to update status: " + e.getMessage())
            );
        }
    }
}