package com.ecommerce.unit;

import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.exception.InvalidInputException;
import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Thêm sản phẩm hợp lệ - lưu thành công")
    void test_addProduct_withValidData_shouldSaveSuccessfully() {
        // Arrange
        Product product = new Product("Laptop ASUS", 25000000, 10, "Electronics");
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product result = productService.addProduct(product);

        // Assert
        assertNotNull(result);
        assertEquals("Laptop ASUS", result.getName());
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertEquals("Laptop ASUS", captor.getValue().getName());
    }

    @Test
    @DisplayName("Thêm sản phẩm với tên rỗng - ném exception")
    void test_addProduct_withEmptyName_shouldThrowException() {
        // Arrange
        Product product = new Product("", 25000000, 10, "Electronics");

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> productService.addProduct(product));
        assertEquals("Tên sản phẩm không được để trống", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Thêm sản phẩm với giá âm - ném exception")
    void test_addProduct_withNegativePrice_shouldThrowException() {
        // Arrange
        Product product = new Product("Laptop", -100000, 10, "Electronics");

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> productService.addProduct(product));
        assertEquals("Giá sản phẩm phải lớn hơn 0", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cập nhật tồn kho hợp lệ - cập nhật thành công")
    void test_updateStock_validQuantity_shouldUpdateCorrectly() {
        // Arrange
        Product product = new Product("Laptop", 25000000, 10, "Electronics");
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product result = productService.updateStock(1L, 5);

        // Assert
        assertEquals(15, result.getStock());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Cập nhật tồn kho dẫn đến số âm - ném InsufficientStockException")
    void test_updateStock_negativeResult_shouldThrowInsufficientStockException() {
        // Arrange
        Product product = new Product("Laptop", 25000000, 5, "Electronics");
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        InsufficientStockException exception = assertThrows(InsufficientStockException.class,
                () -> productService.updateStock(1L, -10));
        assertTrue(exception.getMessage().contains("Tồn kho không đủ"));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Tìm kiếm theo danh mục - không phân biệt hoa thường")
    void test_searchByCategory_caseInsensitive_shouldReturnResults() {
        // Arrange
        List<Product> expected = Arrays.asList(
                new Product("Laptop", 25000000, 10, "Electronics"),
                new Product("Phone", 15000000, 20, "Electronics")
        );
        when(productRepository.findByCategoryIgnoreCase("electronics")).thenReturn(expected);

        // Act
        List<Product> result = productService.searchByCategory("electronics");

        // Assert
        assertEquals(2, result.size());
        verify(productRepository).findByCategoryIgnoreCase("electronics");
    }

    @Test
    @DisplayName("Tính giá giảm hợp lệ - trả về giá đúng")
    void test_calculateDiscount_validPercent_shouldReturnCorrectPrice() {
        // Arrange
        Product product = new Product("Laptop", 1000000, 10, "Electronics");

        // Act
        double result = productService.calculateDiscountedPrice(product, 20.0);

        // Assert
        assertEquals(800000, result, 0.01);
    }

    @Test
    @DisplayName("Tính giá giảm trên 100% - ném exception")
    void test_calculateDiscount_above100Percent_shouldThrowException() {
        // Arrange
        Product product = new Product("Laptop", 1000000, 10, "Electronics");

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> productService.calculateDiscountedPrice(product, 150.0));
        assertEquals("Phần trăm giảm giá phải từ 0 đến 100", exception.getMessage());
    }

    // ========== BỔ SUNG: BVA Boundary Tests ==========

    @Test
    @DisplayName("BVA: Thêm sản phẩm với giá = 0 — ném exception (boundary)")
    void test_addProduct_withZeroPrice_shouldThrowException_BVA() {
        // Arrange
        Product product = new Product("Laptop", 0, 10, "Electronics");

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> productService.addProduct(product));
        assertEquals("Giá sản phẩm phải lớn hơn 0", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("BVA: Thêm sản phẩm với stock âm — ném exception")
    void test_addProduct_withNegativeStock_shouldThrowException_BVA() {
        // Arrange
        Product product = new Product("Laptop", 25000000, -1, "Electronics");

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> productService.addProduct(product));
        assertEquals("Tồn kho không được âm", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("BVA: Tính giá giảm 0% — trả về giá gốc (boundary dưới)")
    void test_calculateDiscount_zeroPercent_shouldReturnOriginalPrice_BVA() {
        // Arrange
        Product product = new Product("Laptop", 1000000, 10, "Electronics");

        // Act
        double result = productService.calculateDiscountedPrice(product, 0.0);

        // Assert
        assertEquals(1000000, result, 0.01);
    }

    @Test
    @DisplayName("BVA: Tính giá giảm 100% — trả về 0 (boundary trên)")
    void test_calculateDiscount_100percent_shouldReturnZero_BVA() {
        // Arrange
        Product product = new Product("Laptop", 1000000, 10, "Electronics");

        // Act
        double result = productService.calculateDiscountedPrice(product, 100.0);

        // Assert
        assertEquals(0.0, result, 0.01);
    }

    @Test
    @DisplayName("BVA: Tính giá giảm âm — ném exception (boundary dưới invalid)")
    void test_calculateDiscount_negativePercent_shouldThrowException_BVA() {
        // Arrange
        Product product = new Product("Laptop", 1000000, 10, "Electronics");

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> productService.calculateDiscountedPrice(product, -1.0));
        assertEquals("Phần trăm giảm giá phải từ 0 đến 100", exception.getMessage());
    }

    @Test
    @DisplayName("Tìm kiếm theo tên với keyword rỗng — trả về tất cả sản phẩm")
    void test_searchByName_emptyKeyword_shouldReturnAll() {
        // Arrange
        List<Product> allProducts = Arrays.asList(
                new Product("Laptop", 25000000, 10, "Electronics"),
                new Product("Phone", 15000000, 20, "Electronics")
        );
        when(productRepository.findAll()).thenReturn(allProducts);

        // Act
        List<Product> result = productService.searchByName("");

        // Assert
        assertEquals(2, result.size());
        verify(productRepository).findAll();
        verify(productRepository, never()).findByNameContainingIgnoreCase(any());
    }

    @Test
    @DisplayName("Thêm sản phẩm với tên null — ném exception")
    void test_addProduct_withNullName_shouldThrowException() {
        // Arrange
        Product product = new Product(null, 25000000, 10, "Electronics");

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> productService.addProduct(product));
        assertEquals("Tên sản phẩm không được để trống", exception.getMessage());
        verify(productRepository, never()).save(any());
    }
}
