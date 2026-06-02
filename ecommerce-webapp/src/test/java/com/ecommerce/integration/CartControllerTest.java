package com.ecommerce.integration;

import com.ecommerce.controller.CartController;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.service.CartService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests cho CartController sử dụng MockMvc standalone mode.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CartController Integration Tests")
class CartControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(cartController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    @DisplayName("IT-006: Xem giỏ hàng có items — hiển thị giỏ hàng đúng")
    void test_viewCart_withItems_shouldShowCart() throws Exception {
        // Arrange
        Cart cart = new Cart("standard_user");
        cart.addItem(new CartItem(1L, "Laptop", 25000000, 2));
        when(cartService.getCart("standard_user")).thenReturn(cart);
        when(cartService.getCartTotal("standard_user")).thenReturn(50000000.0);

        // Act & Assert
        mockMvc.perform(get("/cart").principal(() -> "standard_user"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("cart"))
                .andExpect(model().attribute("cartTotal", 50000000.0))
                .andExpect(model().attribute("cartEmpty", false))
                .andExpect(model().attribute("username", "standard_user"));

        verify(cartService).getCart("standard_user");
        verify(cartService).getCartTotal("standard_user");
    }

    @Test
    @DisplayName("IT-006b: Xem giỏ hàng trống — cartEmpty = true")
    void test_viewCart_emptyCart_shouldShowEmpty() throws Exception {
        // Arrange
        Cart cart = new Cart("standard_user");
        when(cartService.getCart("standard_user")).thenReturn(cart);
        when(cartService.getCartTotal("standard_user")).thenReturn(0.0);

        // Act & Assert
        mockMvc.perform(get("/cart").principal(() -> "standard_user"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attribute("cartEmpty", true));
    }

    @Test
    @DisplayName("IT-006c: Xóa sản phẩm khỏi giỏ hàng — redirect về /cart")
    void test_removeFromCart_shouldRedirect() throws Exception {
        // Arrange
        Cart cart = new Cart("standard_user");
        when(cartService.removeFromCart("standard_user", 1L)).thenReturn(cart);

        // Act & Assert
        mockMvc.perform(post("/cart/remove/1").principal(() -> "standard_user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartService).removeFromCart("standard_user", 1L);
    }

    @Test
    @DisplayName("IT-006d: Xóa toàn bộ giỏ hàng — redirect về /cart")
    void test_clearCart_shouldRedirect() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/cart/clear").principal(() -> "standard_user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartService).clearCart("standard_user");
    }
}
