package com.ecommerce.unit;

import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.exception.InvalidInputException;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.CartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService Unit Tests")
class CartServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    @DisplayName("Thêm sản phẩm có tồn kho vào giỏ - thêm thành công")
    void test_addToCart_productInStock_shouldAddSuccessfully() {
        // Arrange
        Product product = new Product("Laptop", 25000000, 10, "Electronics");
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        Cart result = cartService.addToCart("user1", 1L, 2);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(2, result.getItems().get(0).getQuantity());
        assertEquals("Laptop", result.getItems().get(0).getProductName());
    }

    @Test
    @DisplayName("Thêm sản phẩm hết hàng vào giỏ - ném exception")
    void test_addToCart_outOfStock_shouldThrowException() {
        // Arrange
        Product product = new Product("Laptop", 25000000, 0, "Electronics");
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        InsufficientStockException exception = assertThrows(InsufficientStockException.class,
                () -> cartService.addToCart("user2", 1L, 1));
        assertTrue(exception.getMessage().contains("Không đủ tồn kho"));
    }

    @Test
    @DisplayName("Thêm sản phẩm vượt quá tồn kho - ném exception")
    void test_addToCart_exceedsStock_shouldThrowException() {
        // Arrange
        Product product = new Product("Laptop", 25000000, 5, "Electronics");
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        InsufficientStockException exception = assertThrows(InsufficientStockException.class,
                () -> cartService.addToCart("user3", 1L, 10));
        assertTrue(exception.getMessage().contains("Không đủ tồn kho"));
    }

    @Test
    @DisplayName("Xóa sản phẩm có trong giỏ - xóa thành công")
    void test_removeFromCart_existingItem_shouldRemoveSuccessfully() {
        // Arrange
        Product product = new Product("Laptop", 25000000, 10, "Electronics");
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        cartService.addToCart("user4", 1L, 2);

        // Act
        Cart result = cartService.removeFromCart("user4", 1L);

        // Assert
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    @DisplayName("Xóa sản phẩm không có trong giỏ - ném exception")
    void test_removeFromCart_nonExistentItem_shouldThrowException() {
        // Arrange
        Product product = new Product("Laptop", 25000000, 10, "Electronics");
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        cartService.addToCart("user5", 1L, 1);

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> cartService.removeFromCart("user5", 999L));
        assertTrue(exception.getMessage().contains("không có trong giỏ hàng"));
    }

    @Test
    @DisplayName("Xóa toàn bộ giỏ hàng - giỏ trống sau khi xóa")
    void test_clearCart_shouldRemoveAllItems() {
        // Arrange
        Product product1 = new Product("Laptop", 25000000, 10, "Electronics");
        product1.setId(1L);
        Product product2 = new Product("Phone", 15000000, 20, "Electronics");
        product2.setId(2L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        cartService.addToCart("user6", 1L, 1);
        cartService.addToCart("user6", 2L, 1);

        // Act
        cartService.clearCart("user6");

        // Assert
        Cart cart = cartService.getCart("user6");
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    @DisplayName("Tính tổng tiền nhiều sản phẩm - tính đúng")
    void test_getCartTotal_multipleItems_shouldCalculateCorrectly() {
        // Arrange
        Product product1 = new Product("Laptop", 25000000, 10, "Electronics");
        product1.setId(1L);
        Product product2 = new Product("Phone", 15000000, 20, "Electronics");
        product2.setId(2L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        cartService.addToCart("user7", 1L, 2);
        cartService.addToCart("user7", 2L, 3);

        // Act
        double total = cartService.getCartTotal("user7");

        // Assert
        double expected = 25000000 * 2 + 15000000 * 3;
        assertEquals(expected, total, 0.01);
    }

    // ========== BỔ SUNG: BVA Boundary Tests ==========

    @Test
    @DisplayName("BVA: Thêm sản phẩm với số lượng = 0 — ném exception (boundary)")
    void test_addToCart_zeroQuantity_shouldThrowException_BVA() {
        // Arrange
        Product product = new Product("Laptop", 25000000, 10, "Electronics");
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> cartService.addToCart("user_bva1", 1L, 0));
        assertTrue(exception.getMessage().contains("Số lượng phải lớn hơn 0"));
    }

    @Test
    @DisplayName("BVA: Thêm sản phẩm với số lượng âm — ném exception")
    void test_addToCart_negativeQuantity_shouldThrowException_BVA() {
        // Arrange
        Product product = new Product("Laptop", 25000000, 10, "Electronics");
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> cartService.addToCart("user_bva2", 1L, -5));
        assertTrue(exception.getMessage().contains("Số lượng phải lớn hơn 0"));
    }

    @Test
    @DisplayName("Thêm sản phẩm không tồn tại vào giỏ — ném exception")
    void test_addToCart_productNotFound_shouldThrowException() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> cartService.addToCart("user_notfound", 999L, 1));
        assertTrue(exception.getMessage().contains("Sản phẩm không tồn tại"));
    }

    @Test
    @DisplayName("Xóa sản phẩm khỏi giỏ của user không tồn tại — ném exception")
    void test_removeFromCart_nonExistentUser_shouldThrowException() {
        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> cartService.removeFromCart("non_existent_user", 1L));
        assertTrue(exception.getMessage().contains("Giỏ hàng của người dùng không tồn tại"));
    }

    @Test
    @DisplayName("Tính tổng tiền giỏ hàng trống — trả về 0")
    void test_getCartTotal_emptyCart_shouldReturnZero() {
        // Act
        double total = cartService.getCartTotal("empty_cart_user");

        // Assert
        assertEquals(0.0, total, 0.01);
    }

    @Test
    @DisplayName("Thêm cùng sản phẩm 2 lần — cộng dồn số lượng")
    void test_addToCart_sameProductTwice_shouldAccumulateQuantity() {
        // Arrange
        Product product = new Product("Laptop", 25000000, 10, "Electronics");
        product.setId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        cartService.addToCart("user_accumulate", 1L, 2);
        Cart result = cartService.addToCart("user_accumulate", 1L, 3);

        // Assert
        assertEquals(1, result.getItems().size());
        assertEquals(5, result.getItems().get(0).getQuantity());
    }
}
