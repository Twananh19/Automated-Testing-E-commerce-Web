package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * CheckoutOverviewPage - Page Object cho trang Tổng quan đơn hàng.
 * URL: https://www.saucedemo.com/checkout-step-two.html
 *
 * Bước 2 checkout: xem lại đơn hàng trước khi xác nhận (Finish).
 *
 * FIX: Thêm waitForUrlContains("checkout-step-two") trong constructor
 * để đảm bảo trang đã load xong trước khi tìm phần tử.
 * Nguyên nhân lỗi cũ: TimeoutException khi tìm nút Finish trên DOM chưa sẵn sàng.
 */
public class CheckoutOverviewPage extends BasePage {

    // === LOCATORS ===
    private final By finishBtn = By.cssSelector("[data-testid='finish-button']");                              // Nút Finish (hoàn tất đơn hàng)
    private final By totalPrice = By.cssSelector("[data-testid='total-price']");       // Tổng tiền đơn hàng
    private final By itemName = By.cssSelector("[data-testid^='item-name-']");         // Tên sản phẩm

    public CheckoutOverviewPage(WebDriver driver) {
        super(driver);
        // Chờ trang Overview load xong (URL chứa "checkout-overview") trước khi thao tác
        waitForUrlContains("checkout-overview");
    }

    // === ACTION METHODS ===

    /** Click nút Finish để hoàn tất đặt hàng */
    public void clickFinish() {
        click(finishBtn);
    }

    /** Lấy tổng giá trị đơn hàng (bao gồm thuế) */
    public String getTotalPrice() {
        return getText(totalPrice);
    }

    /** Lấy tên sản phẩm trong đơn hàng */
    public String getItemName() {
        return getText(itemName);
    }
}
