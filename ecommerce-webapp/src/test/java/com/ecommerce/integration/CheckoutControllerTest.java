package com.ecommerce.integration;

import com.ecommerce.controller.CheckoutController;
import com.ecommerce.exception.InvalidInputException;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.service.CartService;
import com.ecommerce.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests cho CheckoutController sử dụng MockMvc standalone mode.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CheckoutController Integration Tests")
class CheckoutControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CartService cartService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private CheckoutController checkoutController;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(checkoutController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    @DisplayName("IT-007: Checkout với giỏ hàng trống — hiển thị cảnh báo")
    void test_checkout_emptyCart_shouldShowWarning() throws Exception {
        // Arrange
        Cart emptyCart = new Cart("standard_user");
        when(cartService.getCart("standard_user")).thenReturn(emptyCart);

        // Act & Assert
        mockMvc.perform(get("/checkout").principal(() -> "standard_user"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout"))
                .andExpect(model().attributeExists("warning"))
                .andExpect(model().attribute("orderTotal", 0.0));
    }

    @Test
    @DisplayName("IT-007b: Checkout với giỏ hàng có items — hiển thị tổng tiền")
    void test_checkout_withItems_shouldShowTotal() throws Exception {
        // Arrange
        Cart cart = new Cart("standard_user");
        cart.addItem(new CartItem(1L, "Laptop", 25000000, 1));
        when(cartService.getCart("standard_user")).thenReturn(cart);
        when(cartService.getCartTotal("standard_user")).thenReturn(25000000.0);

        // Act & Assert
        mockMvc.perform(get("/checkout").principal(() -> "standard_user"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout"))
                .andExpect(model().attributeExists("cart"))
                .andExpect(model().attribute("orderTotal", 25000000.0));
    }

    @Test
    @DisplayName("IT-008: Áp dụng voucher hợp lệ — redirect với flash success")
    void test_applyVoucher_valid_shouldRedirectWithSuccess() throws Exception {
        // Arrange
        Cart cart = new Cart("standard_user");
        cart.addItem(new CartItem(1L, "Laptop", 25000000, 1));
        when(cartService.getCart("standard_user")).thenReturn(cart);

        Order order = new Order("standard_user",
                List.of(new OrderItem(1L, "Laptop", 25000000, 1)), 25000000);
        order.setId(1L);
        when(orderService.createOrder(eq("standard_user"), anyList())).thenReturn(order);

        Order discountedOrder = new Order("standard_user",
                List.of(new OrderItem(1L, "Laptop", 25000000, 1)), 22500000);
        discountedOrder.setId(1L);
        when(orderService.applyVoucher(1L, "SAVE10")).thenReturn(discountedOrder);
        when(orderService.findById(1L)).thenReturn(discountedOrder);

        // Act & Assert
        mockMvc.perform(post("/checkout/apply-voucher")
                        .param("voucherCode", "SAVE10")
                        .principal(() -> "standard_user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout"))
                .andExpect(flash().attributeExists("voucherSuccess"))
                .andExpect(flash().attributeExists("discountedTotal"));

        verify(orderService).applyVoucher(1L, "SAVE10");
    }

    @Test
    @DisplayName("IT-009: Áp dụng voucher không hợp lệ — redirect với flash error")
    void test_applyVoucher_invalid_shouldRedirectWithError() throws Exception {
        // Arrange
        Cart cart = new Cart("standard_user");
        cart.addItem(new CartItem(1L, "Laptop", 25000000, 1));
        when(cartService.getCart("standard_user")).thenReturn(cart);

        Order order = new Order("standard_user",
                List.of(new OrderItem(1L, "Laptop", 25000000, 1)), 25000000);
        order.setId(1L);
        when(orderService.createOrder(eq("standard_user"), anyList())).thenReturn(order);
        when(orderService.applyVoucher(1L, "INVALID_CODE"))
                .thenThrow(new InvalidInputException("Mã voucher không hợp lệ: INVALID_CODE"));

        // Act & Assert
        mockMvc.perform(post("/checkout/apply-voucher")
                        .param("voucherCode", "INVALID_CODE")
                        .principal(() -> "standard_user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout"))
                .andExpect(flash().attributeExists("voucherError"));
    }

    @Test
    @DisplayName("IT-010: Đặt hàng từ giỏ hàng — redirect tới trang xác nhận")
    void test_placeOrder_shouldRedirectToConfirmation() throws Exception {
        // Arrange
        Cart cart = new Cart("standard_user");
        cart.addItem(new CartItem(1L, "Laptop", 25000000, 1));
        when(cartService.getCart("standard_user")).thenReturn(cart);

        Order order = new Order("standard_user",
                List.of(new OrderItem(1L, "Laptop", 25000000, 1)), 25000000);
        order.setId(1L);
        when(orderService.createOrder(eq("standard_user"), anyList())).thenReturn(order);

        // Act & Assert
        mockMvc.perform(post("/checkout/place-order").principal(() -> "standard_user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/order-confirmation/1"));

        verify(orderService).createOrder(eq("standard_user"), anyList());
        verify(cartService).clearCart("standard_user");
    }

    @Test
    @DisplayName("IT-010b: Đặt hàng với giỏ trống — redirect về checkout với warning")
    void test_placeOrder_emptyCart_shouldRedirectWithWarning() throws Exception {
        // Arrange
        Cart emptyCart = new Cart("standard_user");
        when(cartService.getCart("standard_user")).thenReturn(emptyCart);

        // Act & Assert
        mockMvc.perform(post("/checkout/place-order").principal(() -> "standard_user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout"))
                .andExpect(flash().attributeExists("warning"));
    }

    @Test
    @DisplayName("IT-010c: Xem trang xác nhận đơn hàng — hiển thị thông tin order")
    void test_orderConfirmation_shouldShowOrderDetails() throws Exception {
        // Arrange
        Order order = new Order("standard_user",
                List.of(new OrderItem(1L, "Laptop", 25000000, 1)), 25000000);
        order.setId(1L);
        when(orderService.findById(1L)).thenReturn(order);

        // Act & Assert
        mockMvc.perform(get("/order-confirmation/1").principal(() -> "standard_user"))
                .andExpect(status().isOk())
                .andExpect(view().name("order-confirmation"))
                .andExpect(model().attributeExists("order"));

        verify(orderService).findById(1L);
    }
}
