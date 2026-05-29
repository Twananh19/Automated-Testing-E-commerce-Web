package com.ecommerce.service;

import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.exception.InvalidInputException;
import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Thêm sản phẩm mới sau khi validate dữ liệu.
     */
    public Product addProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new InvalidInputException("Tên sản phẩm không được để trống");
        }
        if (product.getPrice() <= 0) {
            throw new InvalidInputException("Giá sản phẩm phải lớn hơn 0");
        }
        if (product.getStock() < 0) {
            throw new InvalidInputException("Tồn kho không được âm");
        }
        return productRepository.save(product);
    }

    /**
     * Cập nhật tồn kho của sản phẩm.
     */
    public Product updateStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new InvalidInputException("Không tìm thấy sản phẩm với ID: " + productId));

        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new InsufficientStockException(
                    "Tồn kho không đủ. Hiện tại: " + product.getStock() + ", yêu cầu giảm: " + Math.abs(quantity));
        }
        product.setStock(newStock);
        return productRepository.save(product);
    }

    /**
     * Tìm kiếm sản phẩm theo danh mục (không phân biệt hoa thường).
     */
    public List<Product> searchByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return productRepository.findAll();
        }
        return productRepository.findByCategoryIgnoreCase(category.trim());
    }

    /**
     * Tìm kiếm sản phẩm theo từ khóa trong tên.
     */
    public List<Product> searchByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findAll();
        }
        return productRepository.findByNameContainingIgnoreCase(keyword.trim());
    }

    /**
     * Tính giá sau khi áp dụng giảm giá (discountPercent từ 0 đến 100).
     */
    public double calculateDiscountedPrice(Product product, double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new InvalidInputException("Phần trăm giảm giá phải từ 0 đến 100");
        }
        return product.getPrice() * (1 - discountPercent / 100.0);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Không tìm thấy sản phẩm với ID: " + id));
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }
}
