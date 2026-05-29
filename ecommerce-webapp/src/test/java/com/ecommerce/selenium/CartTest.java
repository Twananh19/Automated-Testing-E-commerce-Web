package com.ecommerce.selenium;

import com.ecommerce.selenium.pages.CartPage;
import com.ecommerce.selenium.pages.HomePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cart E2E Tests")
class CartTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Thêm nhiều sản phẩm - cập nhật số lượng giỏ hàng")
    void test_addMultipleProducts_shouldUpdateCartCount() {
        navigateTo("/");
        HomePage homePage = new HomePage(driver);

        homePage.clickAddToCart(1L);
        homePage.clickAddToCart(2L);

        int count = homePage.getCartCount();
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Xóa sản phẩm khỏi giỏ - sản phẩm không còn hiển thị")
    void test_removeProductFromCart_shouldRecalculateTotal() {
        // Thêm 2 sản phẩm vào giỏ
        navigateTo("/");
        HomePage homePage = new HomePage(driver);
        homePage.clickAddToCart(1L);

        navigateTo("/");
        homePage = new HomePage(driver);
        homePage.clickAddToCart(2L);

        // Vào trang giỏ hàng, verify có items
        navigateTo("/cart");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/cart"));
        CartPage cartPage = new CartPage(driver);
        assertTrue(cartPage.isItemDisplayed(1L));
        assertTrue(cartPage.isItemDisplayed(2L));

        // Xóa sản phẩm 1
        cartPage.removeItem(1L);

        // Sau redirect, verify sản phẩm 1 đã bị xóa, sản phẩm 2 vẫn còn
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/cart"));
        cartPage = new CartPage(driver);
        assertFalse(cartPage.isItemDisplayed(1L));
        assertTrue(cartPage.isItemDisplayed(2L));
    }
}
