package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * InventoryPage - Page Object đại diện cho trang Danh sách sản phẩm.
 * URL: https://www.saucedemo.com/inventory.html
 *
 * Trang này hiển thị sau khi đăng nhập thành công.
 * Cho phép thêm sản phẩm vào giỏ hàng và truy cập giỏ hàng.
 */
public class InventoryPage extends BasePage {

    // === LOCATORS ===
    private final By addToCartBackpackBtn = By.cssSelector("[data-testid='add-to-cart-1']");   // Nút "Add to cart" cho Sauce Labs Backpack
    private final By cartIcon = By.cssSelector("[data-testid='cart-link']");                    // Icon giỏ hàng (góc trên phải)
    private final By cartBadge = By.cssSelector("[data-testid='cart-badge']");                  // Số lượng sản phẩm trên icon giỏ hàng
    private final By pageTitle = By.cssSelector("[data-testid='page-title']");                      // Tiêu đề trang "Products"

    public InventoryPage(WebDriver driver) {
        super(driver);
    }
    
    public void waitForLoad() {
        waitForUrlContains("inventory");
    }

    // === ACTION METHODS ===

    /** Thêm sản phẩm Sauce Labs Backpack vào giỏ hàng */
    public void clickAddToCart() {
        click(addToCartBackpackBtn);
    }

    /** Click vào icon giỏ hàng để chuyển sang trang Cart */
    public void clickCartIcon() {
        click(cartIcon);
    }

    /** Lấy số lượng sản phẩm hiển thị trên badge giỏ hàng */
    public String getCartBadgeCount() {
        return getText(cartBadge);
    }

    /** Lấy tiêu đề trang để verify đã đăng nhập thành công */
    public String getPageTitle() {
        return getText(pageTitle);
    }

    /** Kiểm tra trang Inventory có hiển thị hay không */
    public boolean isPageDisplayed() {
        return isElementDisplayed(pageTitle);
    }
}
