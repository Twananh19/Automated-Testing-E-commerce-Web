package com.ecommerce.integration;

import com.ecommerce.controller.HomeController;
import com.ecommerce.model.Cart;
import com.ecommerce.model.Product;
import com.ecommerce.service.CartService;
import com.ecommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests cho HomeController sử dụng MockMvc standalone mode.
 * Standalone mode cho phép test controller logic mà không cần render Thymeleaf template.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("HomeController Integration Tests")
class HomeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
    }

    @Test
    @DisplayName("IT-001: Truy cập trang chủ — hiển thị danh sách sản phẩm")
    void test_home_authenticated_shouldShowProducts() throws Exception {
        // Arrange
        List<Product> products = Arrays.asList(
                new Product("Laptop Gaming ASUS", 25000000, 15, "Electronics"),
                new Product("iPhone 15 Pro Max", 32000000, 20, "Electronics")
        );
        when(productService.findAll()).thenReturn(products);
        when(cartService.getCart("standard_user")).thenReturn(new Cart("standard_user"));

        // Act & Assert
        mockMvc.perform(get("/").principal(() -> "standard_user"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("cartItemCount", 0))
                .andExpect(model().attribute("username", "standard_user"));

        verify(productService).findAll();
    }

    @Test
    @DisplayName("IT-003: Tìm kiếm sản phẩm với từ khóa — lọc kết quả đúng")
    void test_search_withKeyword_shouldFilterProducts() throws Exception {
        // Arrange
        List<Product> filtered = List.of(new Product("Laptop Gaming ASUS", 25000000, 15, "Electronics"));
        when(productService.searchByName("Laptop")).thenReturn(filtered);
        when(cartService.getCart("standard_user")).thenReturn(new Cart("standard_user"));

        // Act & Assert
        mockMvc.perform(get("/").param("keyword", "Laptop").principal(() -> "standard_user"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("keyword", "Laptop"));

        verify(productService).searchByName("Laptop");
        verify(productService, never()).findAll();
    }

    @Test
    @DisplayName("IT-003b: Tìm kiếm với keyword rỗng — trả về toàn bộ sản phẩm")
    void test_search_emptyKeyword_shouldReturnAllProducts() throws Exception {
        // Arrange
        List<Product> allProducts = Arrays.asList(
                new Product("Laptop", 25000000, 15, "Electronics"),
                new Product("Phone", 15000000, 20, "Electronics")
        );
        when(productService.findAll()).thenReturn(allProducts);
        when(cartService.getCart("standard_user")).thenReturn(new Cart("standard_user"));

        // Act & Assert
        mockMvc.perform(get("/").param("keyword", "").principal(() -> "standard_user"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("products"));

        verify(productService).findAll();
    }

    @Test
    @DisplayName("IT-004: Thêm sản phẩm vào giỏ — redirect về trang chủ")
    void test_addToCart_shouldRedirect() throws Exception {
        // Arrange
        Cart cart = new Cart("standard_user");
        when(cartService.addToCart(eq("standard_user"), eq(1L), eq(1))).thenReturn(cart);

        // Act & Assert
        mockMvc.perform(post("/add-to-cart/1").principal(() -> "standard_user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(cartService).addToCart("standard_user", 1L, 1);
    }
}
