package com.ecommerce.unit;

import com.ecommerce.exception.*;
import com.ecommerce.model.*;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("Tạo đơn hàng với tồn kho đủ - tạo thành công")
    void test_createOrder_sufficientStock_shouldCreateSuccessfully() {
        // Arrange
        Product product = new Product("Laptop", 25000000, 10, "Electronics");
        product.setId(1L);
        CartItem cartItem = new CartItem(1L, "Laptop", 25000000, 2);
        List<CartItem> items = List.of(cartItem);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        // Act
        Order result = orderService.createOrder("user1", items);

        // Assert
        assertNotNull(result);
        assertEquals("user1", result.getUserId());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(50000000, result.getTotalAmount(), 0.01);
        verify(productRepository).save(product);
        assertEquals(8, product.getStock());
    }

    @Test
    @DisplayName("Tạo đơn hàng với tồn kho không đủ - ném exception")
    void test_createOrder_insufficientStock_shouldThrowException() {
        // Arrange
        Product product = new Product("Laptop", 25000000, 2, "Electronics");
        product.setId(1L);
        CartItem cartItem = new CartItem(1L, "Laptop", 25000000, 5);
        List<CartItem> items = List.of(cartItem);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        InsufficientStockException exception = assertThrows(InsufficientStockException.class,
                () -> orderService.createOrder("user1", items));
        assertTrue(exception.getMessage().contains("không đủ tồn kho"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Hủy đơn hàng PENDING - hủy thành công")
    void test_cancelOrder_pendingStatus_shouldCancelSuccessfully() {
        // Arrange
        Product product = new Product("Laptop", 25000000, 8, "Electronics");
        product.setId(1L);
        OrderItem orderItem = new OrderItem(1L, "Laptop", 25000000, 2);
        Order order = new Order("user1", List.of(orderItem), 50000000);
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Order result = orderService.cancelOrder(1L);

        // Assert
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        assertEquals(10, product.getStock());
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Hủy đơn hàng CONFIRMED - ném exception")
    void test_cancelOrder_confirmedStatus_shouldThrowException() {
        // Arrange
        Order order = new Order("user1", List.of(), 50000000);
        order.setId(1L);
        order.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act & Assert
        InvalidOrderStateException exception = assertThrows(InvalidOrderStateException.class,
                () -> orderService.cancelOrder(1L));
        assertTrue(exception.getMessage().contains("Không thể hủy"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Tính tổng tiền dưới 10 items - không giảm giá")
    void test_calculateTotal_below10Items_noDiscount() {
        // Arrange
        List<OrderItem> items = Arrays.asList(
                new OrderItem(1L, "Product A", 100000, 3),
                new OrderItem(2L, "Product B", 200000, 2)
        );

        // Act
        double total = orderService.calculateOrderTotal(items);

        // Assert
        assertEquals(700000, total, 0.01);
    }

    @Test
    @DisplayName("Tính tổng tiền 10-19 items - giảm 5%")
    void test_calculateTotal_10to19Items_apply5PercentDiscount() {
        // Arrange
        List<OrderItem> items = Arrays.asList(
                new OrderItem(1L, "Product A", 100000, 5),
                new OrderItem(2L, "Product B", 200000, 7)
        );

        // Act
        double total = orderService.calculateOrderTotal(items);

        // Assert
        double subtotal = 100000 * 5 + 200000 * 7;
        double expected = subtotal * 0.95;
        assertEquals(expected, total, 0.01);
    }

    @Test
    @DisplayName("Tính tổng tiền >= 20 items - giảm 10%")
    void test_calculateTotal_20orMoreItems_apply10PercentDiscount() {
        // Arrange
        List<OrderItem> items = Arrays.asList(
                new OrderItem(1L, "Product A", 100000, 10),
                new OrderItem(2L, "Product B", 200000, 12)
        );

        // Act
        double total = orderService.calculateOrderTotal(items);

        // Assert
        double subtotal = 100000 * 10 + 200000 * 12;
        double expected = subtotal * 0.90;
        assertEquals(expected, total, 0.01);
    }

    @Test
    @DisplayName("Áp dụng voucher hợp lệ - giảm giá thành công")
    void test_applyVoucher_validCode_shouldApplyDiscount() {
        // Arrange
        Order order = new Order("user1", List.of(), 1000000);
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        Order result = orderService.applyVoucher(1L, "SAVE10");

        // Assert
        assertEquals(900000, result.getTotalAmount(), 0.01);
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Áp dụng voucher không hợp lệ - ném exception")
    void test_applyVoucher_invalidCode_shouldThrowException() {
        // Arrange
        Order order = new Order("user1", List.of(), 1000000);
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> orderService.applyVoucher(1L, "INVALID_CODE"));
        assertTrue(exception.getMessage().contains("không hợp lệ"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Áp dụng voucher hết hạn - ném exception")
    void test_applyVoucher_expiredCode_shouldThrowException() {
        // Arrange
        Order order = new Order("user1", List.of(), 1000000);
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> orderService.applyVoucher(1L, "EXPIRED2024"));
        assertTrue(exception.getMessage().contains("hết hạn"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Xác nhận đơn hàng PENDING - thành công")
    void test_confirmOrder_pendingStatus_shouldConfirmSuccessfully() {
        Order order = new Order("user1", List.of(), 1000000);
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.confirmOrder(1L);

        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Xác nhận đơn hàng không phải PENDING - ném exception")
    void test_confirmOrder_nonPendingStatus_shouldThrowException() {
        Order order = new Order("user1", List.of(), 1000000);
        order.setId(1L);
        order.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        InvalidOrderStateException exception = assertThrows(InvalidOrderStateException.class,
                () -> orderService.confirmOrder(1L));
        assertTrue(exception.getMessage().contains("Không thể xác nhận"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Giao đơn hàng CONFIRMED - thành công")
    void test_shipOrder_confirmedStatus_shouldShipSuccessfully() {
        Order order = new Order("user1", List.of(), 1000000);
        order.setId(1L);
        order.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.shipOrder(1L);

        assertEquals(OrderStatus.SHIPPED, result.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Giao đơn hàng không phải CONFIRMED - ném exception")
    void test_shipOrder_nonConfirmedStatus_shouldThrowException() {
        Order order = new Order("user1", List.of(), 1000000);
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        InvalidOrderStateException exception = assertThrows(InvalidOrderStateException.class,
                () -> orderService.shipOrder(1L));
        assertTrue(exception.getMessage().contains("Không thể giao hàng"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Hoàn tất giao đơn hàng SHIPPED - thành công")
    void test_deliverOrder_shippedStatus_shouldDeliverSuccessfully() {
        Order order = new Order("user1", List.of(), 1000000);
        order.setId(1L);
        order.setStatus(OrderStatus.SHIPPED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.deliverOrder(1L);

        assertEquals(OrderStatus.DELIVERED, result.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Hoàn tất giao đơn hàng không phải SHIPPED - ném exception")
    void test_deliverOrder_nonShippedStatus_shouldThrowException() {
        Order order = new Order("user1", List.of(), 1000000);
        order.setId(1L);
        order.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        InvalidOrderStateException exception = assertThrows(InvalidOrderStateException.class,
                () -> orderService.deliverOrder(1L));
        assertTrue(exception.getMessage().contains("Không thể hoàn tất giao hàng"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Tìm đơn hàng theo ID tồn tại - thành công")
    void test_findById_existingOrder_shouldReturnSuccessfully() {
        Order order = new Order("user1", List.of(), 1000000);
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Tìm đơn hàng theo ID không tồn tại - ném exception")
    void test_findById_nonExistentOrder_shouldThrowException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class,
                () -> orderService.findById(999L));
        assertTrue(exception.getMessage().contains("Không tìm thấy đơn hàng"));
    }
}
