package com.ecommerce.selenium;

import com.ecommerce.selenium.pages.HomePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Home Page E2E Tests")
class HomePageTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Tìm kiếm sản phẩm theo từ khóa - hiển thị kết quả phù hợp")
    void test_searchProduct_byKeyword_shouldShowRelevantResults() {
        navigateTo("/");
        HomePage homePage = new HomePage(driver);

        homePage.searchProduct("Laptop");
        List<WebElement> products = homePage.getProductList();

        assertFalse(products.isEmpty());
        assertTrue(homePage.isProductDisplayed(1L));
    }

    @Test
    @DisplayName("Tìm kiếm không có kết quả - hiển thị thông báo trống")
    void test_searchProduct_noResults_shouldShowEmptyMessage() {
        navigateTo("/");
        HomePage homePage = new HomePage(driver);

        homePage.searchProduct("XYZ_NONEXISTENT_PRODUCT_12345");

        assertTrue(homePage.isEmptyMessageDisplayed());
    }
}
