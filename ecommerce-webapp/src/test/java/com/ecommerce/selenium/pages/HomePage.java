package com.ecommerce.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object cho trang chủ - danh sách sản phẩm và tìm kiếm.
 */
public class HomePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By SEARCH_INPUT = By.id("search-input");
    private static final By SEARCH_BTN = By.id("search-btn");
    private static final By PRODUCT_LIST = By.id("product-list");
    private static final By CART_LINK = By.id("cart-link");
    private static final By CART_COUNT = By.id("cart-count");
    private static final By EMPTY_MESSAGE = By.id("empty-message");
    private static final By PRODUCT_CARDS = By.className("product-card");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public HomePage searchProduct(String keyword) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT));
        input.clear();
        input.sendKeys(keyword);
        driver.findElement(SEARCH_BTN).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(PRODUCT_LIST));
        return this;
    }

    public HomePage clickAddToCart(Long productId) {
        By addBtn = By.id("add-to-cart-" + productId);
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(addBtn));
        button.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(PRODUCT_LIST));
        return this;
    }

    public List<WebElement> getProductList() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(PRODUCT_LIST));
        return driver.findElements(PRODUCT_CARDS);
    }

    public int getCartCount() {
        WebElement count = wait.until(ExpectedConditions.visibilityOfElementLocated(CART_COUNT));
        return Integer.parseInt(count.getText());
    }

    public CartPage goToCart() {
        driver.findElement(CART_LINK).click();
        return new CartPage(driver);
    }

    public boolean isEmptyMessageDisplayed() {
        try {
            return driver.findElement(EMPTY_MESSAGE).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isProductDisplayed(Long productId) {
        try {
            return driver.findElement(By.id("product-" + productId)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
