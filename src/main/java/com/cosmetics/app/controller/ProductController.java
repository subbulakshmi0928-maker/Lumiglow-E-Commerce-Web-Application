package com.cosmetics.app.controller;

import com.cosmetics.app.model.Category;
import com.cosmetics.app.model.Product;
import com.cosmetics.app.repository.CategoryRepository;
import com.cosmetics.app.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;

    public ProductController(ProductRepository productRepo,
                             CategoryRepository categoryRepo) {
        this.productRepo  = productRepo;
        this.categoryRepo = categoryRepo;
    }

    // ── HELPER: find Category from DB by name string ──
    private Category findCategory(Object categoryObj) {
        if (categoryObj == null) return null;

        String categoryName = "";
        if (categoryObj instanceof String) {
            categoryName = (String) categoryObj;
        } else if (categoryObj instanceof Map) {
            Object nameVal = ((Map<?, ?>) categoryObj).get("name");
            categoryName = nameVal != null ? nameVal.toString() : "";
        }

        if (categoryName.isEmpty()) return null;

        final String finalName = categoryName;
        return categoryRepo.findAll()
            .stream()
            .filter(c -> c.getName().equalsIgnoreCase(finalName))
            .findFirst()
            .orElse(null);
    }

    // ── ADD PRODUCT ──
    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody Map<String, Object> body) {
        try {
            Product product = new Product();
            product.setName((String) body.get("name"));
            product.setPrice(Double.parseDouble(body.get("price").toString()));
            product.setQuantity(Integer.parseInt(body.get("quantity").toString()));
            product.setNet_weight((String) body.get("net_weight"));
            product.setDescription((String) body.get("description"));
            product.setImage((String) body.get("image"));
            product.setCategory(findCategory(body.get("category")));

            Product saved = productRepo.save(product);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", saved.getId());
            response.put("message", "Product added successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ── UPDATE PRODUCT ──
@PutMapping("/update/{id}")
public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                       @RequestBody Map<String, Object> body) {
    try {
        Product product = productRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setName((String) body.get("name"));
        product.setPrice(Double.parseDouble(body.get("price").toString()));
        product.setQuantity(Integer.parseInt(body.get("quantity").toString()));
        product.setNet_weight((String) body.get("net_weight"));
        product.setDescription((String) body.get("description"));
        product.setImage((String) body.get("image"));

        // ✅ FIX: Only update category if it is provided in the request
        // Otherwise keep the existing category — don't overwrite with NULL
        if (body.get("category") != null) {
            Category cat = findCategory(body.get("category"));
            if (cat != null) product.setCategory(cat);
        }

        Product saved = productRepo.save(product);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("id", saved.getId());
        response.put("message", "Product updated successfully");
        return ResponseEntity.ok(response);

    } catch (Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
    // ── GET ALL PRODUCTS ──
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    // ── GET BY CATEGORY ──
    @GetMapping("/category/{category}")
    public List<Product> getByCategory(@PathVariable String category) {
        return productRepo.findAll()
                .stream()
                .filter(p -> p.getCategory() != null)
                .filter(p -> p.getCategory().getName().equalsIgnoreCase(category))
                .toList();
    }

    // ── GET SINGLE PRODUCT ──
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    // ── DELETE PRODUCT ──
    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        if (productRepo.existsById(id)) {
            productRepo.deleteById(id);
            return "Product deleted successfully";
        } else {
            return "Product not found";
        }
    }
}