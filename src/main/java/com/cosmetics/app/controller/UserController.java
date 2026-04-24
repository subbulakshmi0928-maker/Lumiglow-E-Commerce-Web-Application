package com.cosmetics.app.controller;

import com.cosmetics.app.model.User;
import com.cosmetics.app.repository.UserRepository;
import com.cosmetics.app.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    // ─── REGISTER ────────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        try {
            // Check if email already exists
            Optional<User> existing = userRepo.findByEmail(user.getEmail());
            if (existing.isPresent()) {
                return ResponseEntity.status(409).body(Map.of(
                        "success", false,
                        "message", "Email already registered"
                ));
            }

            User saved = userRepo.save(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "User registered successfully",
                    "user", Map.of(
                            "id", saved.getId(),
                            "name", saved.getName() != null ? saved.getName() : "",
                            "email", saved.getEmail() != null ? saved.getEmail() : ""
                    )
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Error during registration: " + e.getMessage()
            ));
        }
    }

    // ─── LOGIN ───────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        try {
            Optional<User> userOptional = userRepo.findByEmailAndPassword(
                    body.get("email"),
                    body.get("password")
            );

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String role = "USER";
                String token = jwtUtil.generateToken(user.getEmail(), role);

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Login successful",
                        "token", token,
                        "role", role,
                        "user", Map.of(
                                "id", user.getId(),
                                "name", user.getName() != null ? user.getName() : "",
                                "email", user.getEmail() != null ? user.getEmail() : ""
                        )
                ));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Invalid email or password"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Server error: " + e.getMessage()
            ));
        }
    }

    // ─── GET USER PROFILE BY EMAIL ───────────────────────────────
    @GetMapping("/user/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            Optional<User> userOptional = userRepo.findByEmail(email);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                return ResponseEntity.ok(Map.of(
                        "id",      user.getId(),
                        "name",    user.getName()    != null ? user.getName()    : "",
                        "email",   user.getEmail()   != null ? user.getEmail()   : "",
                        "phone",   user.getPhone()   != null ? user.getPhone()   : "",
                        "address", user.getAddress() != null ? user.getAddress() : ""
                ));
            }

            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "User not found"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Server error: " + e.getMessage()
            ));
        }
    }

    // ─── UPDATE USER PROFILE BY EMAIL ────────────────────────────
    @PutMapping("/update-user/{email}")
    public ResponseEntity<String> updateUser(
            @PathVariable String email,
            @RequestBody Map<String, String> body) {
        try {
            Optional<User> userOptional = userRepo.findByEmail(email);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                if (body.get("name")    != null) user.setName(body.get("name"));
                if (body.get("phone")   != null) user.setPhone(body.get("phone"));
                if (body.get("address") != null) user.setAddress(body.get("address"));

                userRepo.save(user);
                return ResponseEntity.ok("Profile updated successfully");
            }

            return ResponseEntity.status(404).body("User not found");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    // ─── ADMIN: GET ALL USERS ─────────────────────────────────────
    @GetMapping("/admin/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userRepo.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Server error: " + e.getMessage()
            ));
        }
    }

    // ─── ADMIN: DELETE USER BY ID ─────────────────────────────────
    @DeleteMapping("/admin/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            if (userRepo.existsById(id)) {
                userRepo.deleteById(id);
                return ResponseEntity.ok("User deleted successfully");
            }
            return ResponseEntity.status(404).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

}