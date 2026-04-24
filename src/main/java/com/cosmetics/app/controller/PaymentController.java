package com.cosmetics.app.controller;

import com.cosmetics.app.model.Payment;
import com.cosmetics.app.repository.PaymentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentRepository repo;

    public PaymentController(PaymentRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        return repo.findAll();
    }
}