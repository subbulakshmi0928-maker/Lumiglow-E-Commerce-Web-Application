package com.cosmetics.app.repository;

import com.cosmetics.app.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Find all products by category ID — useful for filtering
    List<Product> findByCategoryId(Long categoryId);
}