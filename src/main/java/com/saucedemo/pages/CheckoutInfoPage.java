package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * CheckoutInfoPage - Page Object cho trang Nhập thông tin giao hàng.
 * URL: https://www.saucedemo.com/checkout-step-one.html
 *
 * Bước 1 của quy trình checkout: nhập First Name, Last Name, Zip Code.
 * Kỹ thuật kiểm thử áp dụng: Decision Table Testing
 */
public class CheckoutInfoPage extends BasePage {

    // === LOCATORS ===
    private final By firstNameInput = By.cssSelector("[data-testid='first-name']");     // Ô nhập Họ
    private final By lastNameInput = By.cssSelector("[data-testid='last-name']");       // Ô nhập Tên
    private final By zipCodeInput = By.cssSelector("[data-testid='postal-code']");      // Ô nhập Mã bưu điện
    private final By continueBtn = By.cssSelector("[data-testid='continue-button']");          // Nút Continue
    private final By errorMessage = By.cssSelector("[data-testid='error-message']"); // Thông báo lỗi

    public CheckoutInfoPage(WebDriver driver) {
        super(driver);
    }

    // === ACTION METHODS ===

    /**
     * Nhập đầy đủ thông tin giao hàng và nhấn Continue.
     * @param firstName Họ
     * @param lastName  Tên
     * @param zipCode   Mã bưu điện
     */
    public void enterShippingInfo(String firstName, String lastName, String zipCode) {
        enterText(firstNameInput, firstName);
        enterText(lastNameInput, lastName);
        enterText(zipCodeInput, zipCode);
        click(continueBtn);
    }

    /** Click nút Continue mà không nhập thông tin - dùng cho negative test */
    public void clickContinue() {
        click(continueBtn);
    }

    /** Lấy thông báo lỗi validation */
    public String getErrorMessage() {
        return getText(errorMessage);
    }
}
