package com.ecommerce.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object cho trang xác nhận đơn hàng.
 */
public class OrderConfirmationPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By ORDER_ID = By.id("order-id");
    private static final By ORDER_STATUS = By.id("order-status");
    private static final By TOTAL_AMOUNT = By.id("total-amount");
    private static final By BACK_HOME_BTN = By.id("back-home-btn");
    private static final By CONFIRMATION_TITLE = By.id("confirmation-title");

    public OrderConfirmationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String getOrderId() {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(ORDER_ID));
        return element.getText();
    }

    public String getOrderStatus() {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(ORDER_STATUS));
        return element.getText();
    }

    public String getTotalAmount() {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(TOTAL_AMOUNT));
        return element.getText();
    }

    public boolean isConfirmationDisplayed() {
        try {
            WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(CONFIRMATION_TITLE));
            return title.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public HomePage goBackHome() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(BACK_HOME_BTN));
        btn.click();
        return new HomePage(driver);
    }
}
