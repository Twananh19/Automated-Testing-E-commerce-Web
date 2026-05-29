package com.ecommerce.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object cho trang thanh toán.
 */
public class CheckoutPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By VOUCHER_INPUT = By.id("voucher-input");
    private static final By APPLY_VOUCHER_BTN = By.id("apply-voucher-btn");
    private static final By PLACE_ORDER_BTN = By.id("place-order-btn");
    private static final By VOUCHER_MESSAGE = By.id("voucher-message");
    private static final By ORDER_TOTAL = By.id("order-total");
    private static final By CHECKOUT_WARNING = By.id("checkout-warning");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public CheckoutPage applyVoucher(String code) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(VOUCHER_INPUT));
        input.clear();
        input.sendKeys(code);
        driver.findElement(APPLY_VOUCHER_BTN).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(VOUCHER_MESSAGE));
        return new CheckoutPage(driver);
    }

    public String getVoucherMessage() {
        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(VOUCHER_MESSAGE));
        return message.getText();
    }

    public String getOrderTotal() {
        WebElement total = wait.until(ExpectedConditions.visibilityOfElementLocated(ORDER_TOTAL));
        return total.getText();
    }

    public OrderConfirmationPage placeOrder() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(PLACE_ORDER_BTN));
        btn.click();
        return new OrderConfirmationPage(driver);
    }

    public boolean hasWarning() {
        try {
            return driver.findElement(CHECKOUT_WARNING).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getWarningMessage() {
        WebElement warning = wait.until(ExpectedConditions.visibilityOfElementLocated(CHECKOUT_WARNING));
        return warning.getText();
    }
}
