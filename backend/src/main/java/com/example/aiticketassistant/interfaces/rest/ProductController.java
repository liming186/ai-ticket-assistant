package com.example.aiticketassistant.interfaces.rest;

import com.example.aiticketassistant.domain.catalog.ProductRepository;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public Map<String, Object> list() {
        return Map.of("products", productRepository.findActiveProducts().stream()
                .map(product -> Map.of(
                        "id", product.id(),
                        "name", product.name(),
                        "price", product.price()))
                .toList());
    }
}
