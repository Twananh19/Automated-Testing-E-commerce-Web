package com.ecommerce.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object cho trang giỏ hàng.
 */
public class CartPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By CART_TOTAL = By.id("cart-total");
    private static final By CHECKOUT_BTN = By.id("checkout-btn");
    private static final By CLEAR_CART_BTN = By.id("clear-cart-btn");
    private static final By CART_ITEMS = By.id("cart-items");
    private static final By EMPTY_CART = By.id("empty-cart");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String getCartTotal() {
        WebElement total = wait.until(ExpectedConditions.visibilityOfElementLocated(CART_TOTAL));
        return total.getText();
    }

    public CartPage removeItem(Long productId) {
        By removeBtn = By.id("remove-" + productId);
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(removeBtn));
        button.click();
        return new CartPage(driver);
    }

    public CheckoutPage clickCheckout() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(CHECKOUT_BTN));
        btn.click();
        return new CheckoutPage(driver);
    }

    public CartPage clearCart() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(CLEAR_CART_BTN));
        btn.click();
        return new CartPage(driver);
    }

    public boolean isCartEmpty() {
        try {
            return driver.findElement(EMPTY_CART).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasItems() {
        try {
            return driver.findElement(CART_ITEMS).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isItemDisplayed(Long productId) {
        try {
            return driver.findElement(By.id("cart-item-" + productId)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
