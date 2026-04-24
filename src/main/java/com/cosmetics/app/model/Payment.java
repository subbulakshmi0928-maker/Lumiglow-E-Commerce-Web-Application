package com.cosmetics.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "payments")   // IMPORTANT: your real table name
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "user_email")
    private String userEmail;

    private Double amount;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "txn_id")
    private String txnId;

    @Column(name = "payment_status")
    private String paymentStatus;

    private String date;

    // ===== GETTERS =====

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Double getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getTxnId() {
        return txnId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getDate() {
        return date;
    }

    // ===== SETTERS =====

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setDate(String date) {
        this.date = date;
    }
}