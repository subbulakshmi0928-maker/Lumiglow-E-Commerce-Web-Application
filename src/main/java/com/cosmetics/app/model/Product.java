package com.cosmetics.app.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price;
    private int quantity;

    @Column(name = "net_weight")
    private String net_weight;

    private String description;
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnore                        // ✅ stops infinite loop, but allows getCategoryName() below
    private Category category;

    public Product() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getNet_weight() { return net_weight; }
    public void setNet_weight(String net_weight) { this.net_weight = net_weight; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    @JsonIgnore
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    // ✅ THIS is what sends "category": "makeup" in the JSON response
    @JsonProperty("category")
    public String getCategoryName() {
        return category != null ? category.getName().toLowerCase() : null;
    }
}