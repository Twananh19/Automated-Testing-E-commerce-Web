package com.ecommerce.config;

import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            productRepository.save(new Product("Laptop Gaming ASUS", 25000000, 15, "Electronics"));
            productRepository.save(new Product("iPhone 15 Pro Max", 32000000, 20, "Electronics"));
            productRepository.save(new Product("Tai nghe Sony WH-1000XM5", 8500000, 30, "Electronics"));
            productRepository.save(new Product("Áo thun nam Cotton", 250000, 100, "Fashion"));
            productRepository.save(new Product("Quần jeans Levi's", 1200000, 50, "Fashion"));
            productRepository.save(new Product("Giày Nike Air Max", 3500000, 25, "Fashion"));
        }
    }
}
