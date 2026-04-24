package com.cosmetics.app.controller;

import com.cosmetics.app.model.Admin;
import com.cosmetics.app.model.User;
import com.cosmetics.app.repository.AdminRepository;
import com.cosmetics.app.repository.UserRepository;
import com.cosmetics.app.repository.ProductRepository;
import com.cosmetics.app.repository.OrderRepository;
import com.cosmetics.app.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private JwtUtil jwtUtil;

    // ===============================
    // ✅ ADMIN LOGIN
    // ===============================
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {

        String email    = body.get("email");
        String password = body.get("password");

        Optional<Admin> adminOpt = adminRepo.findByEmail(email);

        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();

            if (admin.getPassword().equals(password)) {

                String token = jwtUtil.generateToken(email, "ADMIN");

                return Map.of(
                        "success", true,
                        "message", "Login successful",
                        "token",   token,
                        "role",    "ADMIN"
                );
            }
        }

        return Map.of(
                "success", false,
                "message", "Invalid credentials"
        );
    }

    // ===============================
    // ✅ GET ALL CUSTOMERS
    // ===============================
    @GetMapping("/customers")
    public ResponseEntity<List<User>> getAllCustomers() {
        try {
            List<User> users = userRepo.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // ===============================
    // ✅ DELETE CUSTOMER
    // ===============================
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        try {
            userRepo.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // ===============================
    // ✅ ADMIN DASHBOARD STATS
    // ===============================
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        try {

            long totalCustomers = userRepo.count();
            long totalProducts  = productRepo.count();
            long totalOrders    = orderRepo.count();

            double revenue = 0.0;

            Map<String, Object> stats = Map.of(
                    "inventory", totalProducts,
                    "customers", totalCustomers,
                    "orders", totalOrders,
                    "revenue", revenue
            );

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}