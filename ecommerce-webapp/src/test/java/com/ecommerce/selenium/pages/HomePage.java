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
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(SEARCH_INPUT));
        
        // Use Javascript to set the value directly to bypass Linux ChromeDriver keyboard layout bugs on special characters
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
            "arguments[0].value = arguments[1];" +
            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
            input, keyword
        );
        
        input.submit();
        
        // Wait for page reload / URL update to prevent race conditions
        wait.until(ExpectedConditions.urlContains("keyword=" + keyword));
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(PRODUCT_LIST));
        return this;
    }

    public HomePage clickAddToCart(Long productId) {
        int oldCount = getCartCount();
        By addBtn = By.id("add-to-cart-" + productId);
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(addBtn));
        
        // Use Javascript click to bypass viewport/overlap click interception issues in headless Chrome
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        
        wait.until(d -> {
            try {
                WebElement count = d.findElement(CART_COUNT);
                return Integer.parseInt(count.getText()) > oldCount;
            } catch (Exception e) {
                return false;
            }
        });
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
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(CART_LINK));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
        wait.until(ExpectedConditions.urlContains("/cart"));
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
