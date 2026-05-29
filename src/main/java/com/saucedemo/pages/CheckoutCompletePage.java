package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * CheckoutCompletePage - Page Object cho trang Đặt hàng thành công.
 * URL: https://www.saucedemo.com/checkout-complete.html
 *
 * Trang cuối cùng trong luồng mua hàng - hiển thị thông báo thành công.
 */
public class CheckoutCompletePage extends BasePage {

    // === LOCATORS ===
    private final By successHeader = By.cssSelector("[data-testid='success-header']");     // Tiêu đề "Thank you for your order!"
    private final By backHomeBtn = By.cssSelector("[data-testid='back-home-button']");                // Nút quay về trang sản phẩm

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
        // Chờ trang Complete load xong trước khi lấy thông báo
        waitForUrlContains("checkout-complete");
    }

    // === ACTION METHODS ===

    /** Lấy thông báo đặt hàng thành công */
    public String getSuccessMessage() {
        return getText(successHeader);
    }

    /** Click nút Back Home để quay về trang sản phẩm */
    public void clickBackHome() {
        click(backHomeBtn);
    }
}
